package com.fiap.report.infrastructure.mapper;

import com.fiap.report.usecase.dto.ExtractedComponent;
import com.fiap.report.usecase.dto.IdentifiedRisk;
import com.fiap.report.usecase.dto.GeneratedRecommendation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Mapper responsável por converter dados JSON da IA para objetos de domínio
 * Seguindo Clean Architecture - Infrastructure Layer
 */
@Slf4j
@Component
public class AIAnalysisMapper {

    @SuppressWarnings("unchecked")
    public ExtractedComponent mapToComponent(Map<String, Object> componentData) {
        Map<String, Object> properties = (Map<String, Object>) componentData.get("properties");
        List<String> connections = (List<String>) componentData.get("connections");

        return ExtractedComponent.builder()
                .id((String) componentData.get("id"))
                .name((String) componentData.get("name"))
                .type((String) componentData.get("type"))
                .connections(connections)
                .properties(properties)
                .technology((String) componentData.get("technology"))
                .description((String) componentData.get("description"))
                .build();
    }

    @SuppressWarnings("unchecked")
    public IdentifiedRisk mapToRisk(Map<String, Object> riskData) {
        List<String> mitigation = (List<String>) riskData.get("mitigation");

        return IdentifiedRisk.builder()
                .id((String) riskData.get("id"))
                .description((String) riskData.get("description"))
                .level((String) riskData.get("level"))
                .affectedComponent((String) riskData.get("affectedComponent"))
                .category((String) riskData.get("category"))
                .mitigation(mitigation)
                .severityScore(((Number) riskData.getOrDefault("severityScore", 5)).intValue())
                .impact((String) riskData.get("impact"))
                .build();
    }

    @SuppressWarnings("unchecked")
    public GeneratedRecommendation mapToRecommendation(Map<String, Object> recommendationData) {
        List<String> steps = (List<String>) recommendationData.get("steps");

        return GeneratedRecommendation.builder()
                .id((String) recommendationData.get("id"))
                .description((String) recommendationData.get("description"))
                .targetComponent((String) recommendationData.get("targetComponent"))
                .type((String) recommendationData.get("type"))
                .priority((String) recommendationData.get("priority"))
                .rationale((String) recommendationData.get("rationale"))
                .effort((String) recommendationData.get("effort"))
                .steps(steps)
                .build();
    }
}