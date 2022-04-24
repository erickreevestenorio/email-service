package com.exercise.email.entity;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.hibernate.envers.Audited;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Audited
@Entity
@Table(name = "email_client_config", indexes = {@Index(columnList = "updatedOn")})
public class EmailClientConfig extends BaseIdEntity<String> {

    @Column(nullable = false, unique = true)
    private String name;

    @Type(type = "text")
    private String description;

    @Column(nullable = false)
    private  String baseUrl;

    @Column(nullable = false)
    private  String sendEndpoint;

    @Column(nullable = false)
    private  String apiVersion;

    @Type(type = "text")
    @Column(nullable = false, unique = true)
    private String domainName;

    @Column(nullable = false)
    private  String apiSecret;

    @Column(nullable = false)
    private  String apiKey;

    @Type(type = "text")
    @Column(nullable = false)
    private  String senderEmail;

    @Column(nullable = false)
    private  String contentType;

    @Builder(builderMethodName = "emailClientConfigBuilder")
    public EmailClientConfig(String correlationId, long version, Date createdOn, Date updatedOn, String createdBy, String lastModifiedBy, String id, String name, String description, String baseUrl, String apiVersion, String apiSecret, String apiKey, String senderEmail, String contentType) {
        super(correlationId, version, createdOn, updatedOn, createdBy, lastModifiedBy, id);
        this.name = name;
        this.description = description;
        this.baseUrl = baseUrl;
        this.apiVersion = apiVersion;
        this.apiSecret = apiSecret;
        this.apiKey = apiKey;
        this.senderEmail = senderEmail;
        this.contentType = contentType;
    }
}
