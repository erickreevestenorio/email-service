package com.exercise.email.exception.handler;

public class EmailClientNotSupportedException extends RuntimeException {


    public EmailClientNotSupportedException(String message) {
        super(message);
    }
}
