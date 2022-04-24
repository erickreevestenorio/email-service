package com.exercise.email.client.mailgun;

import com.exercise.email.client.EmailClient;
import com.exercise.email.client.mailgun.config.MailgunEmailClientConfig;
import com.exercise.email.exception.handler.EmailClientException;
import com.exercise.email.exception.handler.MailgunEmailClientException;
import com.exercise.email.model.request.EmailRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import static com.exercise.email.client.EmailClientEnum.MAILGUN;
import static com.exercise.email.util.BasicAuthUtil.getBasicAuthenticationHeader;
import static java.lang.String.format;
import static java.time.temporal.ChronoUnit.SECONDS;
import static java.util.Objects.isNull;
import static java.util.Optional.ofNullable;
import static org.apache.commons.lang3.BooleanUtils.isFalse;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MailgunEmailClient implements EmailClient<Map<String, String>> {

    private static final String URL_SEPARATOR = "/";

    private static final String EMAIL_SEPARATOR = ",";

    private final MailgunEmailClientConfig mailgunEmailClientConfig;

    @Override
    public boolean send(EmailRequest request) throws EmailClientException {

        var mailgunRequest = buildRequest(request);

        var form = mailgunRequest.entrySet()
                .stream()
                .map(e -> e.getKey() + "=" + URLEncoder.encode(e.getValue(), StandardCharsets.UTF_8)).collect(Collectors.joining("&"));

        var urlBuilder = new StringJoiner(URL_SEPARATOR);
        urlBuilder.add(mailgunEmailClientConfig.getMailgunApiBaseUrl()).add(mailgunEmailClientConfig.getMailgunApiVersion())
                .add(mailgunEmailClientConfig.getMailgunApiDomainName())
                .add("messages");

        try {
            var httpRequest = HttpRequest.newBuilder()
                    .uri(new URI(urlBuilder.toString()))
                    .version(HttpClient.Version.HTTP_1_1)
//                    .timeout(Duration.of(1, MILLIS))
                    .timeout(Duration.of(30, SECONDS))
                    .headers(CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                    .header(AUTHORIZATION, getBasicAuthenticationHeader(mailgunEmailClientConfig.getMailgunApiUsername(), mailgunEmailClientConfig.getMailgunApiPassword()))
                    .POST(HttpRequest.BodyPublishers.ofString(form))
                    .build();

            var httpClient = HttpClient.newBuilder()
                    .connectTimeout(Duration.of(30, SECONDS))
                    .build();

            log.info("Request {} {}", httpRequest.method(), urlBuilder);

            var response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            if (isNull(response)) {
                throw new MailgunEmailClientException("no response from email client", SERVICE_UNAVAILABLE);
            }

            log.info("Response {} {} status-code: {}", httpRequest.method(), urlBuilder, response.statusCode());

            if (OK.value() != response.statusCode()) {
                throw new MailgunEmailClientException(response.body(), HttpStatus.valueOf(response.statusCode()));
            }

        } catch (URISyntaxException | IOException | InterruptedException e) { //NOSONAR
            throw new MailgunEmailClientException(ExceptionUtils.getRootCauseMessage(e), INTERNAL_SERVER_ERROR);
        }

        return true;
    }

    @Override
    public Map<String, String> buildRequest(EmailRequest request) {
        var mailgunRequest = new HashMap<String, String>();
        var from = ofNullable(request.getName()).map(s ->
                        format("%s %s", request.getName(), mailgunEmailClientConfig.getMailgunApiFromEmail()))
                .orElse(mailgunEmailClientConfig.getMailgunApiFromEmail());
        mailgunRequest.put("from", from);
        mailgunRequest.put("subject", request.getSubject());
        mailgunRequest.put("text", String.join(EMAIL_SEPARATOR, request.getBody()));

        if (isFalse(CollectionUtils.isEmpty(request.getTo()))) {
            mailgunRequest.put("to", String.join(EMAIL_SEPARATOR, request.getTo()));
        }

        if (isFalse(CollectionUtils.isEmpty(request.getCc()))) {
            mailgunRequest.put("cc", String.join(EMAIL_SEPARATOR, request.getCc()));
        }

        if (isFalse(CollectionUtils.isEmpty(request.getBcc()))) {
            mailgunRequest.put("bcc", String.join(EMAIL_SEPARATOR, request.getBcc()));
        }
        return mailgunRequest;
    }

    @Override
    public String getName() {
        return MAILGUN.name();
    }

}
