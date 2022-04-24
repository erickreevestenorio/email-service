package com.exercise.email.sevice.impl;

import com.exercise.email.client.EmailClientProvider;
import com.exercise.email.model.request.EmailRequest;
import com.exercise.email.sevice.EmailService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EmailServiceImpl implements EmailService {

    private final EmailClientProvider emailClientProvider;

    private final CircuitBreakerFactory circuitBreakerFactory;

    private final String defaultEmailClient;

    private final String fallbackEmailClient;

    public EmailServiceImpl(EmailClientProvider emailClientProvider,
                            CircuitBreakerFactory circuitBreakerFactory,
                            @Value("${email.client.default}") String defaultEmailClient,
                            @Value("${email.client.fallback}") String fallbackEmailClient) {
        this.emailClientProvider = emailClientProvider;
        this.circuitBreakerFactory = circuitBreakerFactory;
        this.defaultEmailClient = defaultEmailClient;
        this.fallbackEmailClient = fallbackEmailClient;
    }

    @SneakyThrows
    @Override
    public void send(EmailRequest request) {
        var emailClient = emailClientProvider.getClient(defaultEmailClient);
        circuitBreakerFactory.create("send").run(() -> emailClient.send(request), throwable -> {
            var fallbackClient = emailClientProvider.getClient(fallbackEmailClient);
            log.info("executing fallback email client: {}", fallbackEmailClient);
            return fallbackClient.send(request);
        });
    }


}
