package com.fiap.report.domain.recommendation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationData {
    private String id;
    private String description;
    private String targetComponent;
    private RecommendationType type;
    private Priority priority;
    private String rationale;
    private Effort effort;
    private List<String> steps;
}
