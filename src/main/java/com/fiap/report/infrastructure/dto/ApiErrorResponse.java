package com.fiap.report.infrastructure.dto;

public record ApiErrorResponse(String message, String code, String detail) {
}
