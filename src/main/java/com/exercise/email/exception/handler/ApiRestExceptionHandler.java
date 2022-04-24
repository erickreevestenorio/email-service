package com.exercise.email.exception.handler;

import com.exercise.email.client.mailgun.model.response.MailgunErrorResponse;
import com.exercise.email.client.sendgrid.model.response.SendGridErrorResponse;
import com.exercise.email.model.response.error.ApiError;
import com.exercise.email.model.response.error.ApiSubError;
import com.exercise.email.model.response.error.ApiValidationError;
import com.exercise.email.util.CurrentRequestHolder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

import static com.exercise.email.web.rest.interceptor.RequestIdLoggingInterceptor.REQUEST_ID;
import static java.util.Optional.ofNullable;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Slf4j
@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ApiRestExceptionHandler extends ResponseEntityExceptionHandler {

    @Resource(name = "currentRequestHolder")
    CurrentRequestHolder currentRequestHolder;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * {@inheritDoc}
     * <p>
     * Handle MissingServletRequestParameterException. Triggered when a 'required' request
     * parameter is missing.
     */
    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(
            MissingServletRequestParameterException ex, HttpHeaders headers,
            HttpStatus status, WebRequest request) {
        final String error = ex.getParameterName() + " parameter is missing";
        return buildResponseEntity(new ApiError(BAD_REQUEST, error, ex));
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestPart(MissingServletRequestPartException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        final String error = ex.getRequestPartName() + " part is missing";
        return buildResponseEntity(new ApiError(BAD_REQUEST, error, ex));
    }

    /**
     * {@inheritDoc}
     * <p>
     * Handle HttpMediaTypeNotSupportedException. This one triggers when JSON is invalid
     * as well.
     */
    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(
            HttpMediaTypeNotSupportedException ex, HttpHeaders headers, HttpStatus status,
            WebRequest request) {
        final StringBuilder builder = new StringBuilder();
        builder.append(ex.getContentType());
        builder.append(" media type is not supported. Supported media types are ");
        ex.getSupportedMediaTypes().forEach(t -> builder.append(t).append(", "));
        return buildResponseEntity(new ApiError(HttpStatus.UNSUPPORTED_MEDIA_TYPE,
                builder.substring(0, builder.length() - 2), ex));
    }

    @Override
    protected ResponseEntity<Object> handleBindException(
            BindException ex,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request
    ) {
        final ApiError apiError = new ApiError(BAD_REQUEST);
        apiError.setMessage("Validation error");
        apiError.addValidationErrors(ex.getBindingResult().getFieldErrors());
        apiError.addValidationError(ex.getBindingResult().getGlobalErrors());
        return buildResponseEntity(apiError);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Handle MethodArgumentNotValidException. Triggered when an object fails @Valid
     * validation.
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request
    ) {
        final ApiError apiError = new ApiError(BAD_REQUEST);
        apiError.setMessage("Validation error");
        apiError.addValidationErrors(ex.getBindingResult().getFieldErrors());
        apiError.addValidationError(ex.getBindingResult().getGlobalErrors());
        return buildResponseEntity(apiError);
    }

    /**
     * Handles javax.validation.ConstraintViolationException. Thrown when @Validated
     * fails.
     *
     * @param ex the ConstraintViolationException
     * @return the ApiError object
     */
    @ExceptionHandler(javax.validation.ConstraintViolationException.class)
    protected ResponseEntity<Object> handleConstraintViolation(
            javax.validation.ConstraintViolationException ex) {
        final ApiError apiError = new ApiError(BAD_REQUEST);
        apiError.setMessage("Validation error");
        apiError.addValidationErrors(ex.getConstraintViolations());
        return buildResponseEntity(apiError);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Handle HttpMessageNotReadableException. Happens when request JSON is malformed.
     */
    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status,
            WebRequest request) {
        final ServletWebRequest servletWebRequest = (ServletWebRequest) request;
        log.info("{} to {}", servletWebRequest.getHttpMethod(),
                servletWebRequest.getRequest().getServletPath());
        final String error = "Malformed JSON request";
        return buildResponseEntity(new ApiError(HttpStatus.BAD_REQUEST, error, ex));
    }

    /**
     * {@inheritDoc}
     * <p>
     * Handle HttpMessageNotWritableException.
     */
    @Override
    protected ResponseEntity<Object> handleHttpMessageNotWritable(
            HttpMessageNotWritableException ex, HttpHeaders headers, HttpStatus status,
            WebRequest request) {
        final String error = "Error writing JSON output";
        return buildResponseEntity(new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, error, ex));
    }

    /**
     * Handle Exception, handle generic Exception.class
     *
     * @param ex      the Exception
     * @param request a {@link WebRequest} object.
     * @return the ApiError object
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    protected ResponseEntity<Object> handleMethodArgumentTypeMismatch(
            MethodArgumentTypeMismatchException ex, WebRequest request) {
        final ApiError apiError = new ApiError(BAD_REQUEST);
        apiError.setMessage(String.format(
                "The parameter '%s' of value '%s' could not be converted to type '%s'",
                ex.getName(), ex.getValue(), ex.getRequiredType().getSimpleName()));
        apiError.setDebugMessage(ex.getMessage());
        return buildResponseEntity(apiError);
    }

    private ResponseEntity<Object> getObjectResponseEntity(HttpStatus httpStatus, String message) {
        final ApiError apiError = new ApiError(httpStatus);
        apiError.setMessage(message);
        apiError.setDebugMessage(message);
        return buildResponseEntity(apiError);
    }


    /**
     * Handle ServletRequestBindingException. Triggered when a 'required' request
     * header parameter is missing.
     *
     * @param ex      ServletRequestBindingException
     * @param headers HttpHeaders
     * @param status  HttpStatus
     * @param request WebRequest
     * @return the ResponseEntity object
     */
    @Override
    protected ResponseEntity<Object> handleServletRequestBindingException(ServletRequestBindingException ex,
                                                                          HttpHeaders headers, HttpStatus status, WebRequest request) {
        return getObjectResponseEntity(status, ex.getMessage());
    }

    @ExceptionHandler(MailgunEmailClientException.class)
    protected ResponseEntity<Object> handleMailgunEmailClientException(MailgunEmailClientException e) {

        final ApiError apiError = new ApiError(e.getHttpStatus());
        MailgunErrorResponse errorResponse;
        try {
            errorResponse = objectMapper.readValue(e.getMessage(), MailgunErrorResponse.class);
            apiError.setMessage(errorResponse.getMessage());
            apiError.setDebugMessage("something went wrong with email client: mailgun");
        } catch (JsonProcessingException ex) {
            apiError.setMessage(e.getMessage());
            apiError.setDebugMessage(e.getMessage());
        } finally {
            log.error(apiError.getMessage());
        }

        return buildResponseEntity(apiError);
    }

    @ExceptionHandler(SendGridEmailClientException.class)
    protected ResponseEntity<Object> handleSendGridEmailClientException(SendGridEmailClientException e) {
        List<ApiSubError> subErrors = new ArrayList<>();
        final ApiError apiError = new ApiError(e.getHttpStatus());
        SendGridErrorResponse errorResponse;
        try {
            errorResponse = objectMapper.readValue(e.getMessage(), SendGridErrorResponse.class);
            errorResponse.getErrors().forEach(error -> subErrors.add(new ApiValidationError(EMPTY, error.getField(), error.getMessage())));
            apiError.setSubErrors(subErrors);
            apiError.setDebugMessage("something went wrong with email client: sendgrid");
        } catch (JsonProcessingException ex) {
            apiError.setMessage(e.getMessage());
            apiError.setDebugMessage(e.getMessage());
        } finally {
            log.error(apiError.getMessage());
        }

        return buildResponseEntity(apiError);
    }

    @ExceptionHandler(EmailClientNotSupportedException.class)
    protected ResponseEntity<Object> handleEmailClientNotSupportedException(
            EmailClientNotSupportedException e) {
        final ApiError apiError = new ApiError(BAD_REQUEST);
        apiError.setMessage(e.getMessage());
        apiError.setDebugMessage(e.getMessage());
        return buildResponseEntity(apiError);
    }

    private ResponseEntity<Object> buildResponseEntity(ApiError apiError) {
        apiError.setCode(apiError.getStatus().value());
        apiError.setRequestId(ofNullable(currentRequestHolder.getCurrentRequest())
                .map(httpServletRequest -> httpServletRequest.getAttribute(REQUEST_ID))
                .map(Object::toString)
                .orElse(EMPTY));
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }
}
