package com.fiap.report.usecase.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GeneratedRecommendation {
    private String id;
    private String description;
    private String targetComponent;
    private String type;
    private String priority;
    private String rationale;
    private String effort;
    private List<String> steps;
}
