package com.exercise.email.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

import static com.exercise.email.constant.AppConstant.AT_LEAST_ONE_EMAIL;
import static com.exercise.email.constant.AppConstant.EMAIL_REGEX;
import static com.exercise.email.constant.AppConstant.INVALID_EMAIL_FORMAT;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailRequest {

    private String name;

    @Size(min = 1, message = AT_LEAST_ONE_EMAIL)
    @Builder.Default
    private List<@Size(max = 320) @Email(regexp = EMAIL_REGEX, message = INVALID_EMAIL_FORMAT) String> to = new ArrayList<>();

    @Builder.Default
    private List<@Size(max = 320) @Email(regexp = EMAIL_REGEX, message = INVALID_EMAIL_FORMAT) String> cc = new ArrayList<>();

    @Builder.Default
    private List<@Size(max = 320) @Email(regexp = EMAIL_REGEX, message = INVALID_EMAIL_FORMAT) String> bcc = new ArrayList<>();

    @Size(max = 60)
    @NotEmpty
    private String subject;

    @Size(max = 125)
    @NotEmpty
    private String body;

}
