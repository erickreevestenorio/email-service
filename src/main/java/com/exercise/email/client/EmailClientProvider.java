package com.exercise.email.client;

import com.exercise.email.exception.handler.EmailClientNotSupportedException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.lang.String.format;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class EmailClientProvider {

    private final List<EmailClient<?>> emailClients;

    private static final Map<String, EmailClient<?>> emailClientMap = new HashMap<>();

    @PostConstruct
    public void init() {
        emailClients.forEach(emailClient -> emailClientMap.put(emailClient.getName(), emailClient));
    }

    public EmailClient<?> getClient(String name) {
        return Optional.ofNullable(emailClientMap.get(name)).orElseThrow(() ->
                new EmailClientNotSupportedException(format("email client %s is not supported", name)));
    }
}
