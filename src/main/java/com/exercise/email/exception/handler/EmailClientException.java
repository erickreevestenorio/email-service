package com.exercise.email.exception.handler;

public class EmailClientException extends RuntimeException {

    public EmailClientException(String message) {
        super(message);
    }
}
