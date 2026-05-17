package com.fiap.report.domain.report;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class AnalysisReportTest {

    @Test
    void create_createsReportWithRecebidoStatus() {
        UUID diagramId = UUID.randomUUID();
        String userId = "user-123";

        AnalysisReport report = AnalysisReport.create(diagramId, userId);

        assertThat(report).isNotNull();
        assertThat(report.getDiagramId()).isEqualTo(diagramId);
        assertThat(report.getUserId()).isEqualTo(userId);
        assertThat(report.getStatus()).isEqualTo(ReportStatus.RECEBIDO);
        assertThat(report.getGeneratedAt()).isNotNull();
        assertThat(report.getGeneratedBy()).isEqualTo("AI_SERVICE");
    }

    @Test
    void builder_createsValidReport() {
        UUID diagramId = UUID.randomUUID();
        
        AnalysisReport report = AnalysisReport.builder()
                .id(UUID.randomUUID())
                .diagramId(diagramId)
                .userId("user-123")
                .status(ReportStatus.ANALISADO)
                .generatedAt(java.time.LocalDateTime.now())
                .build();

        assertThat(report).isNotNull();
        assertThat(report.getDiagramId()).isEqualTo(diagramId);
        assertThat(report.getStatus()).isEqualTo(ReportStatus.ANALISADO);
    }
}
