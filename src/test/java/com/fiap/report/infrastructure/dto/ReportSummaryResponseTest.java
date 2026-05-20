package com.fiap.report.infrastructure.dto;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ReportSummaryResponseTest {

    @Test
    void builder_createsValidResponse() {
        UUID id = UUID.randomUUID();
        UUID diagramId = UUID.randomUUID();
        LocalDateTime createdAt = LocalDateTime.now();
        
        ReportSummaryResponse response = ReportSummaryResponse.builder()
                .id(id)
                .diagramId(diagramId)
                .userId("user-123")
                .status("ANALISADO")
                .createdAt(createdAt)
                .updatedAt(createdAt)
                .build();

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(id);
        assertThat(response.getDiagramId()).isEqualTo(diagramId);
        assertThat(response.getStatus()).isEqualTo("ANALISADO");
    }
}
