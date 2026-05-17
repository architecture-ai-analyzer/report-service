package com.fiap.report.infrastructure.dto;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SummaryResponseTest {

    @Test
    void builder_createsValidResponse() {
        SummaryResponse response = SummaryResponse.builder()
                .totalComponents(10)
                .securityScore(85)
                .performanceScore(90)
                .architectureScore(80)
                .build();

        assertThat(response).isNotNull();
        assertThat(response.getTotalComponents()).isEqualTo(10);
        assertThat(response.getSecurityScore()).isEqualTo(85);
        assertThat(response.getPerformanceScore()).isEqualTo(90);
        assertThat(response.getArchitectureScore()).isEqualTo(80);
    }

    @Test
    void from_createsResponseFromComponentsAndRisks() {
        List<RiskResponse> risks = List.of(
                RiskResponse.builder()
                        .id("risk-1")
                        .description("Test risk")
                        .level("HIGH")
                        .build()
        );

        SummaryResponse response = SummaryResponse.from(5, risks);

        assertThat(response).isNotNull();
        assertThat(response.getTotalComponents()).isEqualTo(5);
    }

    @Test
    void defaultSummary_createsDefaultResponse() {
        SummaryResponse response = SummaryResponse.defaultSummary();

        assertThat(response).isNotNull();
        assertThat(response.getTotalComponents()).isEqualTo(0);
        assertThat(response.getSecurityScore()).isEqualTo(100);
    }
}
