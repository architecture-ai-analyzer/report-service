package com.fiap.report.usecase.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AIAnalysisResult {
    private UUID diagramId;
    private String userId;
    private String templateId;
    private List<ExtractedComponent> extractedComponents;
    private List<IdentifiedRisk> identifiedRisks;
    private List<GeneratedRecommendation> generatedRecommendations;
    private String modelVersion;
    private Double confidenceScore;
    private Long processingTimeMs;
    private LocalDateTime processedAt;
}