package com.fiap.report.infrastructure.controller;

import com.fiap.report.domain.report.AnalysisReport;
import com.fiap.report.usecase.CreateReportUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TestControllerTest {

    private CreateReportUseCase createReportUseCase;
    private TestController controller;

    @BeforeEach
    void setUp() {
        createReportUseCase = mock(CreateReportUseCase.class);
        controller = new TestController(createReportUseCase);
    }

    @Test
    void safeEndpoint_validUUID_returnsOk() {
        UUID uuid = UUID.randomUUID();
        String uuidString = uuid.toString();

        var response = controller.safeEndpoint(uuidString);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains(uuid.toString());
    }

    @Test
    void safeEndpoint_invalidUUID_returnsBadRequest() {
        var response = controller.safeEndpoint("invalid-uuid");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).contains("Erro");
    }

    @Test
    void simulateAIResponse_success_returnsOk() {
        AnalysisReport report = AnalysisReport.create(UUID.randomUUID(), "test-user");
        when(createReportUseCase.execute(any(UUID.class), any())).thenReturn(report);

        var response = controller.simulateAIResponse();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("Relatório de teste criado");
    }

    @Test
    void simulateAIResponse_failure_returnsBadRequest() {
        when(createReportUseCase.execute(any(UUID.class), any()))
                .thenThrow(new RuntimeException("Test error"));

        var response = controller.simulateAIResponse();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).contains("Erro");
    }
}
