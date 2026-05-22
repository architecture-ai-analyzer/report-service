package com.fiap.report.domain.risk;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class RiskDataTest {

    @Test
    void builder_createsValidRiskData() {
        RiskData risk = RiskData.builder()
                .id("risk-1")
                .description("Single point of failure")
                .level(RiskLevel.HIGH)
                .affectedComponent("database")
                .category(RiskCategory.RELIABILITY)
                .mitigation(List.of("Add redundancy", "Use clustering"))
                .severityScore(8)
                .impact("HIGH")
                .build();

        assertThat(risk).isNotNull();
        assertThat(risk.getId()).isEqualTo("risk-1");
        assertThat(risk.getDescription()).isEqualTo("Single point of failure");
        assertThat(risk.getLevel()).isEqualTo(RiskLevel.HIGH);
        assertThat(risk.getCategory()).isEqualTo(RiskCategory.RELIABILITY);
        assertThat(risk.getSeverityScore()).isEqualTo(8);
        assertThat(risk.getImpact()).isEqualTo("HIGH");
    }
}
