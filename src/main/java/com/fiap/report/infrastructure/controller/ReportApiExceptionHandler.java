package com.fiap.report.infrastructure.controller;

import com.fiap.report.infrastructure.config.observability.TraceSupport;
import com.fiap.report.infrastructure.dto.ApiErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ReportApiExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(ReportApiExceptionHandler.class);

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        return errorResponse(ex, "BAD_REQUEST", HttpStatus.BAD_REQUEST, "Invalid request", ex.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiErrorResponse> handleRuntime(RuntimeException ex) {
        if (ex.getMessage() != null && ex.getMessage().contains("not found")) {
            return errorResponse(ex, "NOT_FOUND", HttpStatus.NOT_FOUND, "Resource not found", ex.getMessage());
        }
        return errorResponse(
                ex,
                "INTERNAL_ERROR",
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Unexpected error",
                ex.getMessage() != null ? ex.getMessage() : "An unexpected error occurred");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleUnexpected(Exception ex) {
        return errorResponse(
                ex,
                "INTERNAL_ERROR",
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Unexpected error",
                "An unexpected error occurred while processing the request");
    }

    private ResponseEntity<ApiErrorResponse> errorResponse(
            Exception ex,
            String code,
            HttpStatus status,
            String message,
            String detail) {
        TraceSupport.addErrorToSpan(ex, code);
        TraceSupport.putErrorMdc(ex, code, status.value());
        log.error("{} - Code: {} - Detail: {}", ex.getClass().getSimpleName(), code, detail, ex);
        TraceSupport.clearErrorMdc();

        return ResponseEntity.status(status)
                .body(new ApiErrorResponse(message, code, detail));
    }
}
