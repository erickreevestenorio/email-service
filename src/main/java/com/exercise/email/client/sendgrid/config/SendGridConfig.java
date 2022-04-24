package com.exercise.email.client.sendgrid.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Getter
@Setter
@Configuration
@PropertySource("classpath:/email/client/sendgrid/sendgrid-${spring.profiles.active}.properties")
public class SendGridConfig {

    private final String baseUrl;

    private final String version;

    private final String sendEndpoint;

    private final String apiKey;

    private final String senderEmail;

    private final String contentType;

    public SendGridConfig(@Value("${sendgrid.api.base-url}") String baseUrl,
                          @Value("${sendgrid.api.version}") String version,
                          @Value("${sendgrid.api.send-endpoint}") String sendEndpoint,
                          @Value("${sendgrid.api.api-key}") String apiKey,
                          @Value("${sendgrid.api.sender-email}") String senderEmail,
                          @Value("${sendgrid.api.content-type}")String contentType) {
        this.baseUrl = baseUrl;
        this.version = version;
        this.sendEndpoint = sendEndpoint;
        this.apiKey = apiKey;
        this.senderEmail = senderEmail;
        this.contentType = contentType;
    }
}
