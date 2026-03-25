package com.fiap.report.infrastructure.dto;

import com.fiap.report.domain.report.AnalysisReport;
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
public class ReportResponse {
    private UUID id;
    private UUID diagramId;
    private String userId;
    private List<ComponentResponse> components;
    private List<RiskResponse> risks;
    private List<RecommendationResponse> recommendations;
    private String aiModelVersion;
    private Double confidenceScore;
    private Long processingTimeMs;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ReportResponse from(AnalysisReport report) {
        return ReportResponse.builder()
                .id(report.getId())
                .diagramId(report.getDiagramId())
                .userId(report.getUserId())
                .components(report.getComponents().stream()
                        .map(ComponentResponse::from)
                        .toList())
                .risks(report.getRisks().stream()
                        .map(RiskResponse::from)
                        .toList())
                .recommendations(report.getRecommendations().stream()
                        .map(RecommendationResponse::from)
                        .toList())
                .aiModelVersion(report.getAiModelVersion())
                .confidenceScore(report.getConfidenceScore())
                .processingTimeMs(report.getProcessingTimeMs())
                .status(report.getStatus().name())
                .createdAt(report.getGeneratedAt())
                .updatedAt(report.getGeneratedAt())
                .build();
    }
}
