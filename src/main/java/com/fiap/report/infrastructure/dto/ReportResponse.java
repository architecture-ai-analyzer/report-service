package com.fiap.report.infrastructure.dto;

import com.fiap.report.domain.report.AnalysisReport;
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
public class ReportResponse {
    private String generatedAt;
    private SummaryResponse summary;
    private List<ComponentResponse> detectedComponents;
    private SecurityAnalysisResponse securityAnalysis;
    private ArchitectureAnalysisResponse architectureAnalysis;
    private PerformanceAnalysisResponse performanceAnalysis;
    private List<Recommendation> recommendations;

    public static ReportResponse from(AnalysisReport report) {
        List<ComponentResponse> components = report.getComponents() != null
                ? report.getComponents().stream().map(ComponentResponse::from).toList()
                : List.of();

        List<RiskResponse> risks = report.getRisks() != null
                ? report.getRisks().stream().map(RiskResponse::from).toList()
                : List.of();

        List<Recommendation> recommendations = report.getRecommendations() != null
                ? report.getRecommendations().stream().map(Recommendation::from).toList()
                : List.of();

        SummaryResponse summary = components.isEmpty() || risks.isEmpty()
                ? SummaryResponse.defaultSummary()
                : SummaryResponse.from(components.size(), risks);

        SecurityAnalysisResponse securityAnalysis = SecurityAnalysisResponse.from(risks);
        ArchitectureAnalysisResponse architectureAnalysis = ArchitectureAnalysisResponse.from(components);
        PerformanceAnalysisResponse performanceAnalysis = PerformanceAnalysisResponse.from(components);

        return ReportResponse.builder()
                .generatedAt(report.getGeneratedAt() != null ? report.getGeneratedAt().toString() : "2026-03-29T06:04:20.698397Z")
                .summary(summary)
                .detectedComponents(components)
                .securityAnalysis(securityAnalysis)
                .architectureAnalysis(architectureAnalysis)
                .performanceAnalysis(performanceAnalysis)
                .recommendations(recommendations)
                .build();
    }
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class Recommendation {
    private String title;
    private String description;
    private String priority;
    private String category;
    private String effort;
    private String impact;
    private List<String> steps;

    public static Recommendation from(RecommendationData recommendation) {
        return Recommendation.builder()
                .title(recommendation.getDescription())
                .description(recommendation.getRationale())
                .priority(recommendation.getPriority() != null ? recommendation.getPriority().getDisplayName() : "MEDIUM")
                .category(recommendation.getType() != null ? recommendation.getType().getDisplayName() : "MONITORING")
                .effort(recommendation.getEffort() != null ? recommendation.getEffort().getDisplayName() : "MEDIUM")
                .impact("Melhora a arquitetura e segurança")
                .steps(recommendation.getSteps())
                .build();
    }
}