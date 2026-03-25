package com.fiap.report.infrastructure.repository;

import com.fiap.report.domain.component.ComponentData;
import com.fiap.report.domain.recommendation.RecommendationData;
import com.fiap.report.domain.report.AnalysisReport;
import com.fiap.report.domain.report.ReportStatus;
import com.fiap.report.domain.risk.RiskData;
import com.fiap.report.infrastructure.converter.ComponentDataListConverter;
import com.fiap.report.infrastructure.converter.RecommendationDataListConverter;
import com.fiap.report.infrastructure.converter.RiskDataListConverter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "analysis_reports")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalysisReportEntity {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "diagram_id", nullable = false, unique = true)
    private UUID diagramId;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "components", columnDefinition = "jsonb")
    @Convert(converter = ComponentDataListConverter.class)
    private List<ComponentData> components;

    @Column(name = "risks", columnDefinition = "jsonb")
    @Convert(converter = RiskDataListConverter.class)
    private List<RiskData> risks;

    @Column(name = "recommendations", columnDefinition = "jsonb")
    @Convert(converter = RecommendationDataListConverter.class)
    private List<RecommendationData> recommendations;

    @Column(name = "generated_at", nullable = false)
    private LocalDateTime generatedAt;

    @Column(name = "generated_by", nullable = false)
    private String generatedBy;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ReportStatus status;

    @Column(name = "ai_model_version")
    private String aiModelVersion;

    @Column(name = "confidence_score")
    private Double confidenceScore;

    @Column(name = "processing_time_ms")
    private Long processingTimeMs;

    @PrePersist
    public void prePersist() {
        if (id == null) {
            id = UUID.randomUUID();
        }
        if (generatedAt == null) {
            generatedAt = LocalDateTime.now();
        }
        if (generatedBy == null) {
            generatedBy = "AI_SERVICE";
        }
        if (status == null) {
            status = ReportStatus.GENERATED;
        }
    }

    public static AnalysisReportEntity fromDomain(AnalysisReport report) {
        return AnalysisReportEntity.builder()
                .id(report.getId())
                .diagramId(report.getDiagramId())
                .userId(report.getUserId())
                .components(report.getComponents())
                .risks(report.getRisks())
                .recommendations(report.getRecommendations())
                .generatedAt(report.getGeneratedAt())
                .generatedBy(report.getGeneratedBy())
                .status(report.getStatus())
                .aiModelVersion(report.getAiModelVersion())
                .confidenceScore(report.getConfidenceScore())
                .processingTimeMs(report.getProcessingTimeMs())
                .build();
    }

    public AnalysisReport toDomain() {
        return AnalysisReport.builder()
                .id(id)
                .diagramId(diagramId)
                .userId(userId)
                .components(components)
                .risks(risks)
                .recommendations(recommendations)
                .generatedAt(generatedAt)
                .generatedBy(generatedBy)
                .status(status)
                .aiModelVersion(aiModelVersion)
                .confidenceScore(confidenceScore)
                .processingTimeMs(processingTimeMs)
                .build();
    }
}
