package com.fiap.report.infrastructure.controller;

import com.fiap.report.gateway.AnalysisReportGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ReportDeleteControllerTest {

    private AnalysisReportGateway reportGateway;
    private ReportDeleteController controller;

    @BeforeEach
    void setUp() {
        reportGateway = mock(AnalysisReportGateway.class);
        controller = new ReportDeleteController(reportGateway);
    }

    @Test
    void deleteReport_existingReport_returnsNoContent() {
        UUID id = UUID.randomUUID();
        when(reportGateway.existsById(id)).thenReturn(true);

        var response = controller.deleteReport(id);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(reportGateway, times(1)).deleteById(id);
    }

    @Test
    void deleteReport_nonExistingReport_throwsException() {
        UUID id = UUID.randomUUID();
        when(reportGateway.existsById(id)).thenReturn(false);

        assertThatThrownBy(() -> controller.deleteReport(id))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Report not found");
    }
}
