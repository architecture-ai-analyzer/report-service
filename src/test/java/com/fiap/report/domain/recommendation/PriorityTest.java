package com.fiap.report.domain.recommendation;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PriorityTest {

    @Test
    void priority_valuesContainExpectedPriorities() {
        Priority[] priorities = Priority.values();
        
        assertThat(priorities).isNotEmpty();
        assertThat(priorities).contains(Priority.LOW);
        assertThat(priorities).contains(Priority.MEDIUM);
        assertThat(priorities).contains(Priority.HIGH);
        assertThat(priorities).contains(Priority.CRITICAL);
    }

    @Test
    void priority_fromString_returnsCorrectPriority() {
        Priority priority = Priority.valueOf("HIGH");
        
        assertThat(priority).isEqualTo(Priority.HIGH);
    }
}
