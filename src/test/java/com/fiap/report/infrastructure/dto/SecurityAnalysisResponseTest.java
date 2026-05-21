package com.fiap.report.infrastructure.dto;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SecurityAnalysisResponseTest {

    @Test
    void builder_createsValidResponse() {
        SecurityAnalysisResponse response = SecurityAnalysisResponse.builder()
                .overallRisk("Alto")
                .risksFound(3)
                .risks(List.of())
                .compliance(null)
                .build();

        assertThat(response).isNotNull();
        assertThat(response.getOverallRisk()).isEqualTo("Alto");
        assertThat(response.getRisksFound()).isEqualTo(3);
    }

    @Test
    void from_createsResponseFromRisks() {
        List<RiskResponse> risks = List.of(
                RiskResponse.builder()
                        .id("risk-1")
                        .description("Test risk")
                        .level("HIGH")
                        .build()
        );

        SecurityAnalysisResponse response = SecurityAnalysisResponse.from(risks);

        assertThat(response).isNotNull();
        assertThat(response.getOverallRisk()).isEqualTo("Alto");
        assertThat(response.getCompliance()).isNotNull();
    }
}
