package com.exercise.email.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AppConstant {
    public static final String EMAIL_REGEX = "^\\w+([-+.']\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$";
    public static final String INVALID_EMAIL_FORMAT = "invalid email format: ${validatedValue}";
    public static final String AT_LEAST_ONE_EMAIL = "should be at least 1 email";
}
