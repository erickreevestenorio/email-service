package com.exercise.email.config;

import com.exercise.email.util.CurrentRequestHolder;
import com.exercise.email.web.rest.interceptor.LoggingInterceptor;
import com.exercise.email.web.rest.interceptor.RequestIdLoggingInterceptor;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.context.annotation.RequestScope;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.time.Duration;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;
import static com.fasterxml.jackson.databind.DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL;

@Configuration
@PropertySource("classpath:email-service-${spring.profiles.active}.properties")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AppConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoggingInterceptor());
        registry.addInterceptor(requestIdLoggingInterceptor());
    }

    @Bean
    public RequestIdLoggingInterceptor requestIdLoggingInterceptor() {
        return new RequestIdLoggingInterceptor();
    }

    @Bean
    @RequestScope
    public CurrentRequestHolder currentRequestHolder() {
        /*
         * Using this bean to hold current request.
         * Inside RequestIdLoggingInterceptor, for each request this bean is initialized with current request, holding request id.
         * Inside LogsRequestIdInjector, checking for request id, and append it into logs.
         */
        return new CurrentRequestHolder();
    }

    @Bean
    public ObjectMapper objectMapper() {
        var objectMapper = new ObjectMapper();
        objectMapper.configure(FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(READ_UNKNOWN_ENUM_VALUES_AS_NULL, true);
        objectMapper.writerWithDefaultPrettyPrinter();
        return objectMapper;
    }

    @Bean
    public Customizer<Resilience4JCircuitBreakerFactory> defaultCustomizer() {
        return factory -> factory.configureDefault(id -> new Resilience4JConfigBuilder(id)
                .timeLimiterConfig(TimeLimiterConfig.custom().timeoutDuration(Duration.ofSeconds(3)).build())
                .circuitBreakerConfig(CircuitBreakerConfig.ofDefaults())
                .build());
    }

}
