package com.fiap.report.application.impl;

import com.fiap.report.gateway.AnalysisReportGateway;
import com.fiap.report.domain.report.AnalysisReport;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GetReportUseCaseImplTest {

    @Test
    void execute_whenFound_returnsReport() {
        AnalysisReportGateway repo = mock(AnalysisReportGateway.class);
        UUID id = UUID.randomUUID();
        AnalysisReport r = AnalysisReport.create(id, "u");
        when(repo.findByDiagramId(id)).thenReturn(Optional.of(r));

        GetReportUseCaseImpl useCase = new GetReportUseCaseImpl(repo);
        var result = useCase.execute(id);
        assertThat(result).isEqualTo(r);
    }

    @Test
    void execute_whenNotFound_throws() {
        AnalysisReportGateway repo = mock(AnalysisReportGateway.class);
        UUID id = UUID.randomUUID();
        when(repo.findByDiagramId(id)).thenReturn(Optional.empty());

        GetReportUseCaseImpl useCase = new GetReportUseCaseImpl(repo);
        assertThatThrownBy(() -> useCase.execute(id)).hasMessageContaining("Report not found");
    }
}
