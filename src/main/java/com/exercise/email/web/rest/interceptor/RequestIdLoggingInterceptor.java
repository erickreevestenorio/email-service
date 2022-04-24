package com.exercise.email.web.rest.interceptor;

import com.exercise.email.util.CurrentRequestHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

import static java.util.Optional.ofNullable;
import static java.util.function.Predicate.not;

@Slf4j
public class RequestIdLoggingInterceptor implements HandlerInterceptor {

    public static final String REQUEST_ID = "request_id";

    @Resource(name = "currentRequestHolder")
    CurrentRequestHolder currentRequestHolder;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        //A unique Request Id associated with current request
        final var requestId = UUID.randomUUID().toString().replace("-", StringUtils.EMPTY);
        request.setAttribute(REQUEST_ID, requestId);
        ofNullable(currentRequestHolder).ifPresent(crh -> crh.setCurrentRequest(request));
        return true;
    }


    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        long startTime = ofNullable(request.getAttribute("startTime")).map(Object::toString).map(Long::valueOf)
                .orElse(-1L);
        if (startTime > 0) {
            long timeLapsed = System.currentTimeMillis() - startTime;
            log.info("{} {} timeLapsed: {}ms status-code: {}", request.getMethod(), request.getRequestURI(), timeLapsed, response.getStatus());
            ofNullable(ex).ifPresent(e -> log.error(e.getMessage(), e));
        }
        ofNullable(MDC.get(REQUEST_ID))
                .filter(not(String::isEmpty))
                .ifPresent(requestId -> MDC.remove(REQUEST_ID));
    }
}
