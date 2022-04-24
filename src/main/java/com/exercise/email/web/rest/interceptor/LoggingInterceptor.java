package com.exercise.email.web.rest.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static java.util.Optional.ofNullable;

@Slf4j
public class LoggingInterceptor extends HandlerInterceptorAdapter {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        long startTime = System.currentTimeMillis();
        log.info("{} {} at: {}", request.getMethod(), request.getRequestURI(), LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

        request.setAttribute("startTime", startTime);
        return super.preHandle(request, response, handler);
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
    }
}
