package com.fiap.report.domain.recommendation;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class RecommendationDataTest {

    @Test
    void builder_createsValidRecommendationData() {
        RecommendationData recommendation = RecommendationData.builder()
                .id("rec-1")
                .description("Add caching layer")
                .targetComponent("api-gateway")
                .type(RecommendationType.PERFORMANCE)
                .priority(Priority.HIGH)
                .rationale("Reduce database load")
                .effort(Effort.MEDIUM)
                .steps(List.of("Install Redis", "Configure cache"))
                .build();

        assertThat(recommendation).isNotNull();
        assertThat(recommendation.getId()).isEqualTo("rec-1");
        assertThat(recommendation.getDescription()).isEqualTo("Add caching layer");
        assertThat(recommendation.getType()).isEqualTo(RecommendationType.PERFORMANCE);
        assertThat(recommendation.getPriority()).isEqualTo(Priority.HIGH);
        assertThat(recommendation.getEffort()).isEqualTo(Effort.MEDIUM);
        assertThat(recommendation.getSteps()).containsExactly("Install Redis", "Configure cache");
    }
}
