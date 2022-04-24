package com.exercise.email.controller;

import com.exercise.email.ApiErrorResponse;
import com.exercise.email.model.request.EmailRequest;
import com.exercise.email.model.response.EmailResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.CollectionUtils;

import java.util.List;

import static com.exercise.email.constant.AppConstant.AT_LEAST_ONE_EMAIL;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;

@SpringBootTest(properties = "spring.profiles.active:test",
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc(addFilters = false)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class EmailControllerTests {

    final String EMAILS_API = "/api/v1/emails";

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @DisplayName("Test successful send email")
    void test_successful_send_email() throws Exception {
        MvcResult response = mockMvc.perform(MockMvcRequestBuilders
                        .post(EMAILS_API)
                        .content(objectMapper.writeValueAsString(EmailRequest.builder()
                                .name("Erick Reeves Tenorio")
                                .subject("Unit test subject")
                                .to(List.of("erick.reeves.tenorio@gmail.com"))
                                .body("Unit test body")
                                .build()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();
        assertNotNull(response.getResponse());
        assertEquals(OK.value(), response.getResponse().getStatus());
        assertTrue(StringUtils.isNotEmpty(response.getResponse().getContentAsString()));
        var responseJson = response.getResponse().getContentAsString();
        assertDoesNotThrow(() -> {
            var emailResponse = objectMapper.readValue(responseJson, EmailResponse.class);
            assertNotNull(emailResponse);
            assertTrue(StringUtils.isNotEmpty(emailResponse.getRequestId()));
        });
    }

    @Test
    @DisplayName("Test bad request: subject is empty")
    void test_bad_request_subject_is_empty() throws Exception {
        MvcResult response = mockMvc.perform(MockMvcRequestBuilders
                        .post(EMAILS_API)
                        .content(objectMapper.writeValueAsString(EmailRequest.builder()
                                .name("Erick Reeves Tenorio")
                                .to(List.of("erick.reeves.tenorio@gmail.com"))
                                .body("Unit test body")
                                .build()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();
        assertNotNull(response.getResponse());
        assertEquals(BAD_REQUEST.value(), response.getResponse().getStatus());
        assertTrue(StringUtils.isNotEmpty(response.getResponse().getContentAsString()));
        var responseJson = response.getResponse().getContentAsString();
        assertDoesNotThrow(() -> {
            var apiError = objectMapper.readValue(responseJson, ApiErrorResponse.class);
            assertNotNull(apiError);
            assertTrue(StringUtils.isNotEmpty(apiError.getRequestId()));
            assertEquals(BAD_REQUEST, apiError.getStatus());
            assertEquals("Validation error", apiError.getMessage());
            assertFalse(CollectionUtils.isEmpty(apiError.getSubErrors()));
            assertEquals(1, apiError.getSubErrors().size());
            var subError = apiError.getSubErrors().stream().findFirst()
                    .orElse(new ApiErrorResponse.ApiValidationError());
            assertEquals("subject", subError.getField());
            assertEquals("must not be empty", subError.getMessage());
        });
    }

    @Test
    @DisplayName("Test bad request: to is empty")
    void test_bad_request_to_is_empty() throws Exception {
        MvcResult response = mockMvc.perform(MockMvcRequestBuilders
                        .post(EMAILS_API)
                        .content(objectMapper.writeValueAsString(EmailRequest.builder()
                                .subject("Unit test subject")
                                .name("Erick Reeves Tenorio")
                                .body("Unit test body")
                                .build()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();
        assertNotNull(response.getResponse());
        assertEquals(BAD_REQUEST.value(), response.getResponse().getStatus());
        assertTrue(StringUtils.isNotEmpty(response.getResponse().getContentAsString()));
        var responseJson = response.getResponse().getContentAsString();
        assertDoesNotThrow(() -> {
            var apiError = objectMapper.readValue(responseJson, ApiErrorResponse.class);
            assertNotNull(apiError);
            assertTrue(StringUtils.isNotEmpty(apiError.getRequestId()));
            assertEquals(BAD_REQUEST, apiError.getStatus());
            assertEquals("Validation error", apiError.getMessage());
            assertFalse(CollectionUtils.isEmpty(apiError.getSubErrors()));
            assertEquals(1, apiError.getSubErrors().size());
            var subError = apiError.getSubErrors().stream().findFirst()
                    .orElse(new ApiErrorResponse.ApiValidationError());
            assertEquals("to", subError.getField());
            assertEquals(AT_LEAST_ONE_EMAIL, subError.getMessage());
        });
    }

    @Test
    @DisplayName("Test bad request: \"to\" invalid email format")
    void test_bad_request_to_invalid_email_format() throws Exception {
        MvcResult response = mockMvc.perform(MockMvcRequestBuilders
                        .post(EMAILS_API)
                        .content(objectMapper.writeValueAsString(EmailRequest.builder()
                                .subject("Unit test subject")
                                .name("Erick Reeves Tenorio")
                                .to(List.of("erick.reeves.tenorio"))
                                .body("Unit test body")
                                .build()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();
        assertNotNull(response.getResponse());
        assertEquals(BAD_REQUEST.value(), response.getResponse().getStatus());
        assertTrue(StringUtils.isNotEmpty(response.getResponse().getContentAsString()));
        var responseJson = response.getResponse().getContentAsString();
        assertDoesNotThrow(() -> {
            var apiError = objectMapper.readValue(responseJson, ApiErrorResponse.class);
            assertNotNull(apiError);
            assertTrue(StringUtils.isNotEmpty(apiError.getRequestId()));
            assertEquals(BAD_REQUEST, apiError.getStatus());
            assertEquals("Validation error", apiError.getMessage());
            assertFalse(CollectionUtils.isEmpty(apiError.getSubErrors()));
            assertEquals(1, apiError.getSubErrors().size());
            var subError = apiError.getSubErrors().stream().findFirst()
                    .orElse(new ApiErrorResponse.ApiValidationError());
            assertEquals("to[0]", subError.getField());
            assertEquals("invalid email format: erick.reeves.tenorio", subError.getMessage());
        });
    }

    @Test
    @DisplayName("Test bad request: \"cc\" invalid email format")
    void test_bad_request_cc_invalid_email_format() throws Exception {
        MvcResult response = mockMvc.perform(MockMvcRequestBuilders
                        .post(EMAILS_API)
                        .content(objectMapper.writeValueAsString(EmailRequest.builder()
                                .subject("Unit test subject")
                                .name("Erick Reeves Tenorio")
                                .to(List.of("erick.reeves.tenorio@gmail.com"))
                                .cc(List.of("erick.reeves.tenorio"))
                                .body("Unit test body")
                                .build()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();
        assertNotNull(response.getResponse());
        assertEquals(BAD_REQUEST.value(), response.getResponse().getStatus());
        assertTrue(StringUtils.isNotEmpty(response.getResponse().getContentAsString()));
        var responseJson = response.getResponse().getContentAsString();
        assertDoesNotThrow(() -> {
            var apiError = objectMapper.readValue(responseJson, ApiErrorResponse.class);
            assertNotNull(apiError);
            assertTrue(StringUtils.isNotEmpty(apiError.getRequestId()));
            assertEquals(BAD_REQUEST, apiError.getStatus());
            assertEquals("Validation error", apiError.getMessage());
            assertFalse(CollectionUtils.isEmpty(apiError.getSubErrors()));
            assertEquals(1, apiError.getSubErrors().size());
            var subError = apiError.getSubErrors().stream().findFirst()
                    .orElse(new ApiErrorResponse.ApiValidationError());
            assertEquals("cc[0]", subError.getField());
            assertEquals("invalid email format: erick.reeves.tenorio", subError.getMessage());
        });
    }

    @Test
    @DisplayName("Test bad request: \"bcc\" invalid email format")
    void test_bad_request_bcc_invalid_email_format() throws Exception {
        MvcResult response = mockMvc.perform(MockMvcRequestBuilders
                        .post(EMAILS_API)
                        .content(objectMapper.writeValueAsString(EmailRequest.builder()
                                .subject("Unit test subject")
                                .name("Erick Reeves Tenorio")
                                .to(List.of("erick.reeves.tenorio@gmail.com"))
                                .bcc(List.of("erick.reeves.tenorio"))
                                .body("Unit test body")
                                .build()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();
        assertNotNull(response.getResponse());
        assertEquals(BAD_REQUEST.value(), response.getResponse().getStatus());
        assertTrue(StringUtils.isNotEmpty(response.getResponse().getContentAsString()));
        var responseJson = response.getResponse().getContentAsString();
        assertDoesNotThrow(() -> {
            var apiError = objectMapper.readValue(responseJson, ApiErrorResponse.class);
            assertNotNull(apiError);
            assertTrue(StringUtils.isNotEmpty(apiError.getRequestId()));
            assertEquals(BAD_REQUEST, apiError.getStatus());
            assertEquals("Validation error", apiError.getMessage());
            assertFalse(CollectionUtils.isEmpty(apiError.getSubErrors()));
            assertEquals(1, apiError.getSubErrors().size());
            var subError = apiError.getSubErrors().stream().findFirst()
                    .orElse(new ApiErrorResponse.ApiValidationError());
            assertEquals("bcc[0]", subError.getField());
            assertEquals("invalid email format: erick.reeves.tenorio", subError.getMessage());
        });
    }

    @Test
    @DisplayName("Test bad request: \"subject\" invalid size")
    void test_bad_request_subject_invalid_size() throws Exception {
        MvcResult response = mockMvc.perform(MockMvcRequestBuilders
                        .post(EMAILS_API)
                        .content(objectMapper.writeValueAsString(EmailRequest.builder()
                                .subject("Test 2 subjectTest 2 subjectTest 2 subjectTest Test 2 subjectTest 2 subjectTest 2 subjectTest ")
                                .name("Erick Reeves Tenorio")
                                .to(List.of("erick.reeves.tenorio@gmail.com"))
                                .body("Unit test body")
                                .build()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();
        assertNotNull(response.getResponse());
        assertEquals(BAD_REQUEST.value(), response.getResponse().getStatus());
        assertTrue(StringUtils.isNotEmpty(response.getResponse().getContentAsString()));
        var responseJson = response.getResponse().getContentAsString();
        assertDoesNotThrow(() -> {
            var apiError = objectMapper.readValue(responseJson, ApiErrorResponse.class);
            assertNotNull(apiError);
            assertTrue(StringUtils.isNotEmpty(apiError.getRequestId()));
            assertEquals(BAD_REQUEST, apiError.getStatus());
            assertEquals("Validation error", apiError.getMessage());
            assertFalse(CollectionUtils.isEmpty(apiError.getSubErrors()));
            assertEquals(1, apiError.getSubErrors().size());
            var subError = apiError.getSubErrors().stream().findFirst()
                    .orElse(new ApiErrorResponse.ApiValidationError());
            assertEquals("subject", subError.getField());
            assertEquals("size must be between 0 and 60", subError.getMessage());
        });
    }

}
