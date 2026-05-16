package com.fiap.report.infrastructure.controller;

import com.fiap.report.domain.component.ComponentData;
import com.fiap.report.domain.component.ComponentType;
import com.fiap.report.domain.recommendation.Effort;
import com.fiap.report.domain.recommendation.Priority;
import com.fiap.report.domain.recommendation.RecommendationData;
import com.fiap.report.domain.recommendation.RecommendationType;
import com.fiap.report.domain.risk.RiskCategory;
import com.fiap.report.domain.risk.RiskData;
import com.fiap.report.domain.risk.RiskLevel;
import com.fiap.report.domain.report.AnalysisReport;
import com.fiap.report.domain.report.ReportStatus;
import com.fiap.report.gateway.StatusGateway;
import com.fiap.report.infrastructure.mapper.AIAnalysisMapper;
import com.fiap.report.infrastructure.dto.ReportResponse;
import com.fiap.report.usecase.CreateReportUseCase;
import com.fiap.report.usecase.FindReportByDiagramIdUseCase;
import com.fiap.report.usecase.GetReportUseCase;
import com.fiap.report.usecase.ListReportsUseCase;
import com.fiap.report.usecase.dto.AIAnalysisResult;
import com.fiap.report.usecase.dto.ExtractedComponent;
import com.fiap.report.usecase.dto.GeneratedRecommendation;
import com.fiap.report.usecase.dto.IdentifiedRisk;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReportControllerTest {

    @Mock
    private CreateReportUseCase createReportUseCase;

    @Mock
    private FindReportByDiagramIdUseCase findReportByDiagramIdUseCase;

    @Mock
    private GetReportUseCase getReportUseCase;

    @Mock
    private ListReportsUseCase listReportsUseCase;

    @Mock
    private StatusGateway statusGateway;

    @Mock
    private AIAnalysisMapper aiAnalysisMapper;

    private ReportController controller;

    @BeforeEach
    void setUp() {
        controller = new ReportController(createReportUseCase, findReportByDiagramIdUseCase,
                getReportUseCase, listReportsUseCase, statusGateway, aiAnalysisMapper);
    }

    @Test
    void shouldGenerateReportAndReturnOk() {
        String uploadId = "upload-123";
        UUID diagramId = UUID.nameUUIDFromBytes(uploadId.getBytes());

        Map<String, Object> requestBody = Map.of(
                "analysis", Map.of(
                        "components", List.of(Map.of(
                                "id", "comp-1",
                                "name", "API Gateway",
                                "type", "API",
                                "connections", List.of("user-service"),
                                "properties", Map.of("protocol", "HTTP"),
                                "technology", "Spring Boot",
                                "description", "Gateway"
                        )),
                        "risks", List.of(Map.of(
                                "id", "risk-1",
                                "description", "Falha",
                                "level", "HIGH",
                                "affectedComponent", "comp-1",
                                "category", "SECURITY",
                                "mitigation", List.of("Redundância"),
                                "severityScore", 9,
                                "impact", "Disponibilidade"
                        )),
                        "recommendations", List.of(Map.of(
                                "id", "rec-1",
                                "description", "Melhorar cache",
                                "targetComponent", "comp-1",
                                "type", "PERFORMANCE",
                                "priority", "HIGH",
                                "rationale", "Reduzir latência",
                                "effort", "LOW",
                                "steps", List.of("Ajustar TTL")
                        ))
                ),
                "metadata", Map.of(
                        "userId", "user-123",
                        "modelVersion", "gpt-4",
                        "confidenceScore", 0.95,
                        "processingTimeMs", 2100
                )
        );

        when(findReportByDiagramIdUseCase.execute(diagramId)).thenReturn(Optional.empty());

        ExtractedComponent component = ExtractedComponent.builder()
                .id("comp-1")
                .name("API Gateway")
                .type("API")
                .connections(List.of("user-service"))
                .properties(Map.of("protocol", "HTTP"))
                .technology("Spring Boot")
                .description("Gateway")
                .build();
        IdentifiedRisk risk = IdentifiedRisk.builder()
                .id("risk-1")
                .description("Falha")
                .level("HIGH")
                .affectedComponent("comp-1")
                .category("SECURITY")
                .mitigation(List.of("Redundância"))
                .severityScore(9)
                .impact("Disponibilidade")
                .build();
        GeneratedRecommendation recommendation = GeneratedRecommendation.builder()
                .id("rec-1")
                .description("Melhorar cache")
                .targetComponent("comp-1")
                .type("PERFORMANCE")
                .priority("HIGH")
                .rationale("Reduzir latência")
                .effort("LOW")
                .steps(List.of("Ajustar TTL"))
                .build();

        when(aiAnalysisMapper.mapToComponent(any())).thenReturn(component);
        when(aiAnalysisMapper.mapToRisk(any())).thenReturn(risk);
        when(aiAnalysisMapper.mapToRecommendation(any())).thenReturn(recommendation);

        AnalysisReport report = AnalysisReport.builder()
                .id(UUID.randomUUID())
                .diagramId(diagramId)
                .userId("user-123")
                .generatedAt(LocalDateTime.now())
                .status(ReportStatus.ANALISADO)
                .components(List.of(ComponentData.builder()
                        .id("comp-1")
                        .name("API Gateway")
                        .type(ComponentType.API)
                        .connections(List.of("user-service"))
                        .properties(Map.of("protocol", "HTTP"))
                        .technology("Spring Boot")
                        .description("Gateway")
                        .build()))
                .risks(List.of(RiskData.builder()
                        .id("risk-1")
                        .description("Falha")
                        .level(RiskLevel.HIGH)
                        .affectedComponent("comp-1")
                        .category(RiskCategory.SECURITY)
                        .mitigation(List.of("Redundância"))
                        .severityScore(9)
                        .impact("Disponibilidade")
                        .build()))
                .recommendations(List.of(RecommendationData.builder()
                        .id("rec-1")
                        .description("Melhorar cache")
                        .targetComponent("comp-1")
                        .type(RecommendationType.PERFORMANCE)
                        .priority(Priority.HIGH)
                        .rationale("Reduzir latência")
                        .effort(Effort.LOW)
                        .steps(List.of("Ajustar TTL"))
                        .build()))
                .build();

        when(createReportUseCase.execute(eq(diagramId), any(AIAnalysisResult.class))).thenReturn(report);

        ResponseEntity<ReportResponse> response = controller.generateReport(uploadId, requestBody);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getDetectedComponents()).hasSize(1);
        assertThat(response.getBody().getSecurityAnalysis()).isNotNull();
        assertThat(response.getBody().getRecommendations()).hasSize(1);
    }
}
