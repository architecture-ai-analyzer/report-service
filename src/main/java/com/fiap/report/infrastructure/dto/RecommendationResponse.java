package com.fiap.report.infrastructure.dto;

import com.fiap.report.domain.recommendation.RecommendationData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationResponse {
    private String id;
    private String description;
    private String targetComponent;
    private String type;
    private String priority;
    private String rationale;
    private String effort;
    private List<String> steps;

    public static RecommendationResponse from(RecommendationData recommendation) {
        return RecommendationResponse.builder()
                .id(recommendation.getId())
                .description(recommendation.getDescription())
                .targetComponent(recommendation.getTargetComponent())
                .type(recommendation.getType().name())
                .priority(recommendation.getPriority().name())
                .rationale(recommendation.getRationale())
                .effort(recommendation.getEffort().name())
                .steps(recommendation.getSteps())
                .build();
    }
}
