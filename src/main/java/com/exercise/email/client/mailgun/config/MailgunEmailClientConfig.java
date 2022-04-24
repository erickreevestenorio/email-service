package com.exercise.email.client.mailgun.config;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Getter
@Setter
@Configuration
@PropertySource("classpath:/email/client/mailgun/mailgun-${spring.profiles.active}.properties")
public class MailgunEmailClientConfig {

    private final String mailgunApiBaseUrl;

    private final String mailgunApiVersion;

    private final String mailgunApiDomainName;

    private final String mailgunApiUsername;

    private final String mailgunApiPassword;

    private final String mailgunApiFromEmail;

    @Builder
    public MailgunEmailClientConfig(@Value("${mailgun.api.base-url}") String mailgunApiBaseUrl,
                                    @Value("${mailgun.api.version}") String mailgunApiVersion,
                                    @Value("${mailgun.api.domain-name}")String mailgunApiDomainName,
                                    @Value("${mailgun.api.username}") String mailgunApiUsername,
                                    @Value("${mailgun.api.password}") String mailgunApiPassword,
                                    @Value("${mailgun.api.from-email}") String mailgunApiFromEmail) {
        this.mailgunApiBaseUrl = mailgunApiBaseUrl;
        this.mailgunApiVersion = mailgunApiVersion;
        this.mailgunApiDomainName = mailgunApiDomainName;
        this.mailgunApiUsername = mailgunApiUsername;
        this.mailgunApiPassword = mailgunApiPassword;
        this.mailgunApiFromEmail = mailgunApiFromEmail;
    }

}
