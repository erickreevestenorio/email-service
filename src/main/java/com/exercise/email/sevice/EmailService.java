package com.exercise.email.sevice;

import com.exercise.email.model.request.EmailRequest;

public interface EmailService {

    void send(EmailRequest request);

}
