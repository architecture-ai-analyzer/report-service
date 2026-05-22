package com.fiap.report.domain.report;

import com.fiap.report.domain.component.ComponentData;
import com.fiap.report.domain.risk.RiskData;
import com.fiap.report.domain.recommendation.RecommendationData;
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
public class AnalysisReport {

    private UUID id;
    private UUID diagramId;
    private String templateId;
    private List<ComponentData> components;
    private List<RiskData> risks;
    private List<RecommendationData> recommendations;
    private LocalDateTime generatedAt;
    private String generatedBy;
    private ReportStatus status;

    public static AnalysisReport create(UUID diagramId, String templateId) {
        return AnalysisReport.builder()
                .id(UUID.randomUUID())
                .diagramId(diagramId)
                .templateId(templateId)
                .generatedAt(LocalDateTime.now())
                .generatedBy("AI_SERVICE")
                .status(ReportStatus.RECEBIDO)
                .build();
    }
}