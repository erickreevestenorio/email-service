package com.exercise.email.web.rest.controller;

import com.exercise.email.model.request.EmailRequest;
import com.exercise.email.model.response.EmailResponse;
import com.exercise.email.sevice.EmailService;
import com.exercise.email.util.CurrentRequestHolder;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

import static com.exercise.email.web.rest.interceptor.RequestIdLoggingInterceptor.REQUEST_ID;
import static java.util.Optional.ofNullable;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/api/v1/emails")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class EmailController {

    @Resource(name = "currentRequestHolder")
    CurrentRequestHolder currentRequestHolder;

    private final EmailService emailService;

    @PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<EmailResponse> send(@Valid @RequestBody EmailRequest request) {
        emailService.send(request);
        return ResponseEntity.ok(EmailResponse.builder().requestId(ofNullable(currentRequestHolder.getCurrentRequest())
                .map(httpServletRequest -> httpServletRequest.getAttribute(REQUEST_ID))
                .map(Object::toString)
                .orElse(StringUtils.EMPTY)).build());
    }

}
