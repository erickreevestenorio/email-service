package com.exercise.email.client.sendgrid.model.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class SendGridSendEmailRequest {

    @Builder.Default
    private List<Personalization> personalizations = new ArrayList<>();

    private Email from;

    private String subject;

    @Builder.Default
    @JsonProperty("content")
    private List<Content> contents = new ArrayList<>();


    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class Personalization {

        @Builder.Default
        private List<Email> to = new ArrayList<>();

        @Builder.Default
        private List<Email> cc = new ArrayList<>();

        @Builder.Default
        private List<Email> bcc = new ArrayList<>();

    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class Email {

        @JsonProperty("email")
        private String emailAddress;

    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class Content {
        private String type;
        private String value;
    }

}
