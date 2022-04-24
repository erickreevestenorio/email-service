package com.exercise.email.util;

import lombok.Getter;
import lombok.Setter;

import javax.servlet.http.HttpServletRequest;

/**
 * This request scoped bean holds current request, containing request id to be injected into each log printed against
 * current request.
 */
public class CurrentRequestHolder {
    @Getter
    @Setter
    private HttpServletRequest currentRequest;

}
