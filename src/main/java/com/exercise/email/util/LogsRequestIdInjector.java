package com.exercise.email.util;

import ch.qos.logback.classic.pattern.MessageConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import static com.exercise.email.web.rest.interceptor.RequestIdLoggingInterceptor.REQUEST_ID;
import static java.util.Optional.ofNullable;

/**
 * Injecting request id, into each log entry associated with a request. See "conversionRule" tag inside logback.xml file.
 * For more information on logback layout and conversion rule, visit here -> http://logback.qos.ch/manual/layouts.html
 */
@Slf4j
public class LogsRequestIdInjector extends MessageConverter {

    @Override
    public String convert(ILoggingEvent event) {
        return ofNullable(ApplicationContextProvider.getApplicationContext())
                .map(applicationContext -> applicationContext.getBean(CurrentRequestHolder.class))
                .map(CurrentRequestHolder::getCurrentRequest)
                .map(httpServletRequest -> httpServletRequest.getAttribute(REQUEST_ID))
                .map(Object::toString)
                .orElse(StringUtils.EMPTY);
    }

}
