package com.fiap.report.domain.recommendation;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EffortTest {

    @Test
    void effort_valuesContainExpectedEfforts() {
        Effort[] efforts = Effort.values();
        
        assertThat(efforts).isNotEmpty();
        assertThat(efforts).contains(Effort.LOW);
        assertThat(efforts).contains(Effort.MEDIUM);
        assertThat(efforts).contains(Effort.HIGH);
    }

    @Test
    void effort_fromString_returnsCorrectEffort() {
        Effort effort = Effort.valueOf("LOW");
        
        assertThat(effort).isEqualTo(Effort.LOW);
    }
}
