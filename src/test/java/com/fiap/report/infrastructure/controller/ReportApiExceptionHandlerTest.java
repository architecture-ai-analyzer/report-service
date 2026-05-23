package com.fiap.report.infrastructure.controller;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

class ReportApiExceptionHandlerTest {

    private final ReportApiExceptionHandler exceptionHandler = new ReportApiExceptionHandler();

    @Test
    void handleIllegalArgument_shouldReturnBadRequest() {
        IllegalArgumentException ex = new IllegalArgumentException("Invalid parameter");

        var response = exceptionHandler.handleIllegalArgument(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().code()).isEqualTo("BAD_REQUEST");
        assertThat(response.getBody().message()).isEqualTo("Invalid request");
        assertThat(response.getBody().detail()).isEqualTo("Invalid parameter");
    }

    @Test
    void handleRuntime_shouldReturnNotFoundWhenMessageContainsNotFound() {
        RuntimeException ex = new RuntimeException("Resource not found");

        var response = exceptionHandler.handleRuntime(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().code()).isEqualTo("NOT_FOUND");
        assertThat(response.getBody().message()).isEqualTo("Resource not found");
        assertThat(response.getBody().detail()).isEqualTo("Resource not found");
    }

    @Test
    void handleRuntime_shouldReturnInternalServerErrorWhenMessageDoesNotContainNotFound() {
        RuntimeException ex = new RuntimeException("Database error");

        var response = exceptionHandler.handleRuntime(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().code()).isEqualTo("INTERNAL_ERROR");
        assertThat(response.getBody().message()).isEqualTo("Unexpected error");
        assertThat(response.getBody().detail()).isEqualTo("Database error");
    }

    @Test
    void handleRuntime_shouldReturnInternalServerErrorWhenMessageIsNull() {
        RuntimeException ex = new RuntimeException((String) null);

        var response = exceptionHandler.handleRuntime(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().code()).isEqualTo("INTERNAL_ERROR");
        assertThat(response.getBody().message()).isEqualTo("Unexpected error");
        assertThat(response.getBody().detail()).isEqualTo("An unexpected error occurred");
    }

    @Test
    void handleUnexpected_shouldReturnInternalServerError() {
        Exception ex = new Exception("Unexpected error");

        var response = exceptionHandler.handleUnexpected(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().code()).isEqualTo("INTERNAL_ERROR");
        assertThat(response.getBody().message()).isEqualTo("Unexpected error");
        assertThat(response.getBody().detail()).isEqualTo("An unexpected error occurred while processing the request");
    }
}
