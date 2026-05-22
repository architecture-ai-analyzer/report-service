package com.fiap.report.infrastructure.dto;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RiskResponseTest {

    @Test
    void builder_createsValidResponse() {
        RiskResponse response = RiskResponse.builder()
                .id("risk-1")
                .description("Single point of failure")
                .level("HIGH")
                .category("RELIABILITY")
                .affectedComponent("database")
                .severityScore(8)
                .impact("HIGH")
                .build();

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo("risk-1");
        assertThat(response.getDescription()).isEqualTo("Single point of failure");
        assertThat(response.getLevel()).isEqualTo("HIGH");
        assertThat(response.getCategory()).isEqualTo("RELIABILITY");
        assertThat(response.getSeverityScore()).isEqualTo(8);
    }
}
