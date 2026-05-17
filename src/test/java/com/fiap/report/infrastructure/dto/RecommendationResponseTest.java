package com.fiap.report.infrastructure.dto;

import com.fiap.report.domain.recommendation.Effort;
import com.fiap.report.domain.recommendation.Priority;
import com.fiap.report.domain.recommendation.RecommendationData;
import com.fiap.report.domain.recommendation.RecommendationType;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class RecommendationResponseTest {

    @Test
    void from_mapsFieldsCorrectly() {
        RecommendationData r = RecommendationData.builder()
                .id("rec1")
                .description("Use cache")
                .targetComponent("comp-1")
                .type(RecommendationType.PERFORMANCE)
                .priority(Priority.MEDIUM)
                .rationale("reduce latency")
                .effort(Effort.MEDIUM)
                .steps(List.of("Add Redis"))
                .build();

        RecommendationResponse resp = RecommendationResponse.from(r);

        assertThat(resp.getId()).isEqualTo("rec1");
        assertThat(resp.getDescription()).isEqualTo("Use cache");
        assertThat(resp.getTargetComponent()).isEqualTo("comp-1");
        assertThat(resp.getType()).isEqualTo("PERFORMANCE");
        assertThat(resp.getPriority()).isEqualTo("MEDIUM");
        assertThat(resp.getRationale()).isEqualTo("reduce latency");
        assertThat(resp.getEffort()).isEqualTo("MEDIUM");
        assertThat(resp.getSteps()).containsExactly("Add Redis");
    }
}
