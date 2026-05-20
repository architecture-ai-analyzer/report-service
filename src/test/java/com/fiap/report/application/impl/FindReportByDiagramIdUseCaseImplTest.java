package com.fiap.report.application.impl;

import com.fiap.report.gateway.AnalysisReportGateway;
import com.fiap.report.domain.report.AnalysisReport;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class FindReportByDiagramIdUseCaseImplTest {

    @Test
    void execute_returnsOptionalReport() {
        AnalysisReportGateway repo = mock(AnalysisReportGateway.class);
        UUID id = UUID.randomUUID();
        AnalysisReport r = AnalysisReport.create(id, "u");
        when(repo.findByDiagramId(id)).thenReturn(Optional.of(r));

        FindReportByDiagramIdUseCaseImpl useCase = new FindReportByDiagramIdUseCaseImpl(repo);
        var opt = useCase.execute(id);
        assertThat(opt).isPresent().contains(r);
    }
}
