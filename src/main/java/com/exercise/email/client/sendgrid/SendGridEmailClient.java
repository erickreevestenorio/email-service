package com.exercise.email.client.sendgrid;

import com.exercise.email.client.EmailClient;
import com.exercise.email.client.sendgrid.model.request.SendGridSendEmailRequest;
import com.exercise.email.entity.EmailClientConfig;
import com.exercise.email.exception.handler.EmailClientException;
import com.exercise.email.exception.handler.SendGridEmailClientException;
import com.exercise.email.model.request.EmailRequest;
import com.exercise.email.repository.EmailClientConfigRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

import static com.exercise.email.client.EmailClientEnum.SEND_GRID;
import static java.lang.String.format;
import static java.time.temporal.ChronoUnit.SECONDS;
import static java.util.Objects.isNull;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpStatus.ACCEPTED;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE;
import static org.springframework.util.CollectionUtils.isEmpty;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SendGridEmailClient implements EmailClient<SendGridSendEmailRequest> {

    private static final String URL_SEPARATOR = "/";

    private final ObjectMapper objectMapper;

    private final EmailClientConfigRepository emailClientConfigRepository;

    @Override
    public boolean send(EmailRequest request) throws EmailClientException {

        var config = emailClientConfigRepository.findByName(getName()).orElseThrow(() -> new EmailClientException("email client not found"));

        var sendGridSendEmailRequest = buildRequest(request, config);

        var urlBuilder = new StringJoiner(URL_SEPARATOR);

        urlBuilder.add(config.getBaseUrl()).add(config.getApiVersion())
                .add(config.getSendEndpoint());

        try {
            var requestJsonString = objectMapper.writeValueAsString(sendGridSendEmailRequest);
            var httpRequest = HttpRequest.newBuilder()
                    .uri(new URI(urlBuilder.toString()))
                    .version(HttpClient.Version.HTTP_1_1)
                    .timeout(Duration.of(30, SECONDS))
                    .headers(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .header(AUTHORIZATION, format("Bearer %s", config.getApiSecret()))
                    .POST(HttpRequest.BodyPublishers.ofString(requestJsonString))
                    .build();

            var httpClient = HttpClient.newBuilder()
                    .connectTimeout(Duration.of(30, SECONDS))
                    .build();

            log.info("Request {} {}", httpRequest.method(), urlBuilder);

            var response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            if (isNull(response)) {
                throw new SendGridEmailClientException("no response from email client", SERVICE_UNAVAILABLE);
            }

            log.info("Response {} {} status-code: {}", httpRequest.method(), urlBuilder, response.statusCode());

            if (ACCEPTED.value() != response.statusCode()) {
                throw new SendGridEmailClientException(response.body(), HttpStatus.valueOf(response.statusCode()));
            }

        } catch (URISyntaxException | IOException | InterruptedException e) { //NOSONAR
            throw new SendGridEmailClientException(ExceptionUtils.getRootCauseMessage(e), INTERNAL_SERVER_ERROR);
        }

        return true;
    }

    @Override
    public SendGridSendEmailRequest buildRequest(EmailRequest request, EmailClientConfig config) {
        var sendGridSendEmailRequest = SendGridSendEmailRequest.builder()
                .subject(request.getSubject())
                .from(SendGridSendEmailRequest.Email.builder()
                        .emailAddress(config.getSenderEmail())
                        .build())
                .build();

        var content = SendGridSendEmailRequest.Content.builder()
                .type(config.getContentType())
                .value(request.getBody())
                .build();

        sendGridSendEmailRequest.setContents(List.of(content));

        var to = new ArrayList<SendGridSendEmailRequest.Email>();

        request.getTo().forEach(emailAddress -> to.add(SendGridSendEmailRequest.Email.builder()
                .emailAddress(emailAddress)
                .build()));

        var cc = new ArrayList<SendGridSendEmailRequest.Email>();
        request.getCc().forEach(emailAddress -> cc.add(SendGridSendEmailRequest.Email.builder()
                .emailAddress(emailAddress)
                .build()));

        var bcc = new ArrayList<SendGridSendEmailRequest.Email>();
        request.getBcc().forEach(emailAddress -> bcc.add(SendGridSendEmailRequest.Email.builder()
                .emailAddress(emailAddress)
                .build()));

        sendGridSendEmailRequest.getPersonalizations().add(SendGridSendEmailRequest.Personalization.builder()
                .to(to)
                .build());

        if (!isEmpty(cc)) {
            sendGridSendEmailRequest.getPersonalizations().add(SendGridSendEmailRequest.Personalization.builder()
                    .cc(cc)
                    .build());
        }

        if (!isEmpty(bcc)) {
            sendGridSendEmailRequest.getPersonalizations().add(SendGridSendEmailRequest.Personalization.builder()
                    .bcc(bcc)
                    .build());
        }

        return sendGridSendEmailRequest;
    }

    @Override
    public String getName() {
        return SEND_GRID.name();
    }
}
