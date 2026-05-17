package com.fiap.report.domain.risk;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RiskLevelTest {

    @Test
    void riskLevel_valuesContainExpectedLevels() {
        RiskLevel[] levels = RiskLevel.values();
        
        assertThat(levels).isNotEmpty();
        assertThat(levels).contains(RiskLevel.LOW);
        assertThat(levels).contains(RiskLevel.MEDIUM);
        assertThat(levels).contains(RiskLevel.HIGH);
        assertThat(levels).contains(RiskLevel.CRITICAL);
    }

    @Test
    void riskLevel_fromString_returnsCorrectLevel() {
        RiskLevel level = RiskLevel.valueOf("HIGH");
        
        assertThat(level).isEqualTo(RiskLevel.HIGH);
    }
}
