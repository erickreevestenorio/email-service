package com.exercise.email.exception.handler;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class MailgunEmailClientException extends EmailClientException {

    private final HttpStatus httpStatus;

    public MailgunEmailClientException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }
}
