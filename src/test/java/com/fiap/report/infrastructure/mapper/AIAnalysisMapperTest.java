package com.fiap.report.infrastructure.mapper;

import com.fiap.report.usecase.dto.ExtractedComponent;
import com.fiap.report.usecase.dto.GeneratedRecommendation;
import com.fiap.report.usecase.dto.IdentifiedRisk;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class AIAnalysisMapperTest {

    private AIAnalysisMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new AIAnalysisMapper();
    }

    @Test
    void mapToComponent_validData_returnsComponent() {
        Map<String, Object> componentData = Map.of(
                "id", "comp-1",
                "name", "API Gateway",
                "type", "API",
                "connections", List.of("user-service", "order-service"),
                "properties", Map.of("protocol", "HTTP", "port", 8080),
                "technology", "Spring Boot",
                "description", "Main API Gateway"
        );

        ExtractedComponent result = mapper.mapToComponent(componentData);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("comp-1");
        assertThat(result.getName()).isEqualTo("API Gateway");
        assertThat(result.getType()).isEqualTo("API");
        assertThat(result.getConnections()).containsExactly("user-service", "order-service");
        assertThat(result.getProperties()).containsEntry("protocol", "HTTP");
        assertThat(result.getTechnology()).isEqualTo("Spring Boot");
        assertThat(result.getDescription()).isEqualTo("Main API Gateway");
    }

    @Test
    void mapToComponent_withDefaultValues_returnsComponent() {
        Map<String, Object> componentData = Map.of(
                "id", "comp-1",
                "name", "Service",
                "type", "SERVICE"
        );

        ExtractedComponent result = mapper.mapToComponent(componentData);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("comp-1");
        assertThat(result.getConnections()).isNull();
        assertThat(result.getProperties()).isNull();
    }

    @Test
    void mapToRisk_validData_returnsRisk() {
        Map<String, Object> riskData = Map.of(
                "id", "risk-1",
                "description", "Single point of failure",
                "level", "HIGH",
                "affectedComponent", "database",
                "category", "RELIABILITY",
                "mitigation", List.of("Add redundancy", "Use clustering"),
                "severityScore", 8,
                "impact", "HIGH"
        );

        IdentifiedRisk result = mapper.mapToRisk(riskData);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("risk-1");
        assertThat(result.getDescription()).isEqualTo("Single point of failure");
        assertThat(result.getLevel()).isEqualTo("HIGH");
        assertThat(result.getAffectedComponent()).isEqualTo("database");
        assertThat(result.getCategory()).isEqualTo("RELIABILITY");
        assertThat(result.getMitigation()).containsExactly("Add redundancy", "Use clustering");
        assertThat(result.getSeverityScore()).isEqualTo(8);
        assertThat(result.getImpact()).isEqualTo("HIGH");
    }

    @Test
    void mapToRisk_withDefaultSeverityScore_returnsRisk() {
        Map<String, Object> riskData = Map.of(
                "id", "risk-1",
                "description", "Risk",
                "level", "MEDIUM",
                "affectedComponent", "service",
                "category", "SECURITY"
        );

        IdentifiedRisk result = mapper.mapToRisk(riskData);

        assertThat(result).isNotNull();
        assertThat(result.getSeverityScore()).isEqualTo(5); // default value
    }

    @Test
    void mapToRecommendation_validData_returnsRecommendation() {
        Map<String, Object> recommendationData = Map.of(
                "id", "rec-1",
                "description", "Add caching layer",
                "targetComponent", "api-gateway",
                "type", "PERFORMANCE",
                "priority", "HIGH",
                "rationale", "Reduce database load",
                "effort", "MEDIUM",
                "steps", List.of("Install Redis", "Configure cache", "Update code")
        );

        GeneratedRecommendation result = mapper.mapToRecommendation(recommendationData);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("rec-1");
        assertThat(result.getDescription()).isEqualTo("Add caching layer");
        assertThat(result.getTargetComponent()).isEqualTo("api-gateway");
        assertThat(result.getType()).isEqualTo("PERFORMANCE");
        assertThat(result.getPriority()).isEqualTo("HIGH");
        assertThat(result.getRationale()).isEqualTo("Reduce database load");
        assertThat(result.getEffort()).isEqualTo("MEDIUM");
        assertThat(result.getSteps()).containsExactly("Install Redis", "Configure cache", "Update code");
    }
}
