package com.exercise.email.client.sendgrid.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SendGridErrorResponse {

    private List<Error> errors;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Error {
        private String message;
        private String field;
        private String help;
    }

}
