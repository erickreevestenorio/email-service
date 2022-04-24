package com.exercise.email.client;

import com.exercise.email.entity.EmailClientConfig;
import com.exercise.email.exception.handler.EmailClientException;
import com.exercise.email.model.request.EmailRequest;

public interface EmailClient<T> {

    boolean send(EmailRequest request) throws EmailClientException;

    T buildRequest(EmailRequest request, EmailClientConfig emailClientConfig);

    String getName();


}
