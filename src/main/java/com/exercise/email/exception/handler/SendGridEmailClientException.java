package com.exercise.email.exception.handler;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class SendGridEmailClientException extends EmailClientException{

    private final HttpStatus httpStatus;

    public SendGridEmailClientException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }
}
