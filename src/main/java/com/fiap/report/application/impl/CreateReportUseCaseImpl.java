package com.fiap.report.application.impl;

import com.fiap.report.domain.component.ComponentData;
import com.fiap.report.domain.recommendation.Effort;
import com.fiap.report.domain.recommendation.Priority;
import com.fiap.report.domain.recommendation.RecommendationData;
import com.fiap.report.domain.recommendation.RecommendationType;
import com.fiap.report.domain.report.AnalysisReport;
import com.fiap.report.domain.report.ReportStatus;
import com.fiap.report.domain.risk.RiskCategory;
import com.fiap.report.domain.risk.RiskData;
import com.fiap.report.domain.risk.RiskLevel;
import com.fiap.report.usecase.CreateReportUseCase;
import com.fiap.report.usecase.dto.AIAnalysisResult;
import com.fiap.report.usecase.dto.ExtractedComponent;
import com.fiap.report.usecase.dto.IdentifiedRisk;
import com.fiap.report.usecase.dto.GeneratedRecommendation;
import com.fiap.report.gateway.AnalysisReportGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreateReportUseCaseImpl implements CreateReportUseCase {

    private final AnalysisReportGateway repository;

    @Override
    public AnalysisReport execute(UUID diagramId, AIAnalysisResult aiResult) {
        log.info("Creating report for diagram: {}", diagramId);

        try {
            // Converter componentes
            List<ComponentData> components = aiResult.getExtractedComponents().stream()
                    .map(this::convertToComponentData)
                    .toList();

            // Converter riscos
            List<RiskData> risks = aiResult.getIdentifiedRisks().stream()
                    .map(this::convertToRiskData)
                    .toList();

            // Converter recomendações
            List<RecommendationData> recommendations = aiResult.getGeneratedRecommendations().stream()
                    .map(this::convertToRecommendationData)
                    .toList();

            AnalysisReport report = AnalysisReport.builder()
                    .diagramId(diagramId)
                    .userId(aiResult.getUserId())
                    .components(components)
                    .risks(risks)
                    .recommendations(recommendations)
                    .aiModelVersion(aiResult.getModelVersion())
                    .confidenceScore(aiResult.getConfidenceScore())
                    .processingTimeMs(aiResult.getProcessingTimeMs())
                    .status(ReportStatus.ANALISADO)
                    .build();

            return repository.save(report);

        } catch (Exception e) {
            log.error("Error creating report for diagram: {}", diagramId, e);
            throw new RuntimeException("Failed to create report", e);
        }
    }

    private ComponentData convertToComponentData(ExtractedComponent extracted) {
        return ComponentData.builder()
                .id(UUID.randomUUID().toString())
                .name(extracted.getName())
                .type(com.fiap.report.domain.component.ComponentType.valueOf(extracted.getType().toUpperCase()))
                .connections(extracted.getConnections())
                .properties(extracted.getProperties())
                .technology(extracted.getTechnology())
                .description(extracted.getDescription())
                .build();
    }

    private RiskData convertToRiskData(IdentifiedRisk identified) {
        return RiskData.builder()
                .id(identified.getId())
                .description(identified.getDescription())
                .level(RiskLevel.valueOf(identified.getLevel().toUpperCase()))
                .affectedComponent(identified.getAffectedComponent())
                .category(RiskCategory.valueOf(identified.getCategory().toUpperCase()))
                .mitigation(identified.getMitigation())
                .severityScore(identified.getSeverityScore())
                .impact(identified.getImpact())
                .build();
    }

    private RecommendationData convertToRecommendationData(GeneratedRecommendation generated) {
        return RecommendationData.builder()
                .id(generated.getId())
                .description(generated.getDescription())
                .targetComponent(generated.getTargetComponent())
                .type(RecommendationType.valueOf(generated.getType().toUpperCase()))
                .priority(Priority.valueOf(generated.getPriority().toUpperCase()))
                .rationale(generated.getRationale())
                .effort(Effort.valueOf(generated.getEffort().toUpperCase()))
                .steps(generated.getSteps())
                .build();
    }
}
