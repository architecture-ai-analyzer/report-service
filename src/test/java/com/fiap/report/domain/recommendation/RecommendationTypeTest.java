package com.fiap.report.domain.recommendation;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RecommendationTypeTest {

    @Test
    void recommendationType_valuesContainExpectedTypes() {
        RecommendationType[] types = RecommendationType.values();
        
        assertThat(types).isNotEmpty();
        assertThat(types).contains(RecommendationType.PERFORMANCE);
        assertThat(types).contains(RecommendationType.SECURITY);
        assertThat(types).contains(RecommendationType.RELIABILITY);
        assertThat(types).contains(RecommendationType.SCALABILITY);
    }

    @Test
    void recommendationType_fromString_returnsCorrectType() {
        RecommendationType type = RecommendationType.valueOf("PERFORMANCE");
        
        assertThat(type).isEqualTo(RecommendationType.PERFORMANCE);
    }
}
