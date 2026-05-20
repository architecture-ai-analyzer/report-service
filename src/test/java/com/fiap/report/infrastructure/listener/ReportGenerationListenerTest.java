package com.fiap.report.infrastructure.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiap.report.domain.report.AnalysisReport;
import com.fiap.report.domain.report.ReportStatus;
import com.fiap.report.gateway.StatusGateway;
import com.fiap.report.infrastructure.mapper.AIAnalysisMapper;
import com.fiap.report.usecase.CreateReportUseCase;
import com.fiap.report.usecase.dto.AIAnalysisResult;
import com.fiap.report.usecase.dto.ExtractedComponent;
import com.fiap.report.usecase.dto.GeneratedRecommendation;
import com.fiap.report.usecase.dto.IdentifiedRisk;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReportGenerationListenerTest {

    @Mock
    private CreateReportUseCase createReportUseCase;

    @Mock
    private StatusGateway statusGateway;

    @Mock
    private AIAnalysisMapper aiAnalysisMapper;

    private ObjectMapper objectMapper;
    private ReportGenerationListener listener;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        listener = new ReportGenerationListener(createReportUseCase, statusGateway, objectMapper, aiAnalysisMapper);
    }

    @Test
    void shouldProcessMessageAndUpdateStatusToAnalysed() throws Exception {
        UUID diagramId = UUID.randomUUID();
        String uploadId = diagramId.toString();

        Map<String, Object> payload = Map.of(
                "uploadId", uploadId,
                "analysis", Map.of(
                        "components", List.of(
                                Map.of(
                                        "id", "comp-1",
                                        "name", "API Gateway",
                                        "type", "SERVICE",
                                        "connections", List.of("user-service"),
                                        "properties", Map.of("protocol", "HTTP"),
                                        "technology", "Spring Boot",
                                        "description", "Gateway para API"
                                )
                        ),
                        "risks", List.of(
                                Map.of(
                                        "id", "risk-1",
                                        "description", "Falha no gateway",
                                        "level", "HIGH",
                                        "affectedComponent", "comp-1",
                                        "category", "SECURITY",
                                        "mitigation", List.of("Adicionar redundância"),
                                        "severityScore", 8,
                                        "impact", "Disponibilidade"
                                )
                        ),
                        "recommendations", List.of(
                                Map.of(
                                        "id", "rec-1",
                                        "description", "Adicionar cache",
                                        "targetComponent", "comp-1",
                                        "type", "PERFORMANCE",
                                        "priority", "MEDIUM",
                                        "rationale", "Reduzir latência",
                                        "effort", "LOW",
                                        "steps", List.of("Configurar Redis")
                                )
                        )
                ),
                "metadata", Map.of(
                        "userId", "user-123",
                        "modelVersion", "gpt-4",
                        "confidenceScore", 0.92,
                        "processingTimeMs", 1200
                )
        );

        String message = objectMapper.writeValueAsString(payload);

        ExtractedComponent component = ExtractedComponent.builder()
                .id("comp-1")
                .name("API Gateway")
                .type("SERVICE")
                .connections(List.of("user-service"))
                .properties(Map.of("protocol", "HTTP"))
                .technology("Spring Boot")
                .description("Gateway para API")
                .build();

        IdentifiedRisk risk = IdentifiedRisk.builder()
                .id("risk-1")
                .description("Falha no gateway")
                .level("HIGH")
                .affectedComponent("comp-1")
                .category("SECURITY")
                .mitigation(List.of("Adicionar redundância"))
                .severityScore(8)
                .impact("Disponibilidade")
                .build();

        GeneratedRecommendation recommendation = GeneratedRecommendation.builder()
                .id("rec-1")
                .description("Adicionar cache")
                .targetComponent("comp-1")
                .type("PERFORMANCE")
                .priority("MEDIUM")
                .rationale("Reduzir latência")
                .effort("LOW")
                .steps(List.of("Configurar Redis"))
                .build();

        when(aiAnalysisMapper.mapToComponent(any())).thenReturn(component);
        when(aiAnalysisMapper.mapToRisk(any())).thenReturn(risk);
        when(aiAnalysisMapper.mapToRecommendation(any())).thenReturn(recommendation);

        AnalysisReport createdReport = AnalysisReport.builder()
                .id(UUID.randomUUID())
                .diagramId(diagramId)
                .userId("user-123")
                .generatedAt(LocalDateTime.now())
                .status(ReportStatus.ANALISADO)
                .components(List.of())
                .risks(List.of())
                .recommendations(List.of())
                .build();
        when(createReportUseCase.execute(eq(diagramId), any(AIAnalysisResult.class))).thenReturn(createdReport);

        listener.handleReportGeneration(message);

        InOrder inOrder = inOrder(statusGateway, createReportUseCase, statusGateway);
        inOrder.verify(statusGateway).updateStatus(diagramId, "EM_PROCESSAMENTO");
        inOrder.verify(createReportUseCase).execute(eq(diagramId), any(AIAnalysisResult.class));
        inOrder.verify(statusGateway).updateStatus(diagramId, "ANALISADO");
        verify(statusGateway, never()).updateStatus(diagramId, "ERRO");

        ArgumentCaptor<AIAnalysisResult> captor = ArgumentCaptor.forClass(AIAnalysisResult.class);
        verify(createReportUseCase).execute(eq(diagramId), captor.capture());
        AIAnalysisResult actual = captor.getValue();

        assertThat(actual.getDiagramId()).isEqualTo(diagramId);
        assertThat(actual.getUserId()).isEqualTo("user-123");
        assertThat(actual.getModelVersion()).isEqualTo("gpt-4");
        assertThat(actual.getConfidenceScore()).isEqualTo(0.92);
        assertThat(actual.getProcessingTimeMs()).isEqualTo(1200L);
        assertThat(actual.getExtractedComponents()).containsExactly(component);
        assertThat(actual.getIdentifiedRisks()).containsExactly(risk);
        assertThat(actual.getGeneratedRecommendations()).containsExactly(recommendation);
    }

    @Test
    void shouldUpdateStatusToErrorWhenProcessingFails() throws Exception {
        UUID diagramId = UUID.randomUUID();
        String uploadId = diagramId.toString();

        Map<String, Object> payload = Map.of(
                "uploadId", uploadId,
                "analysis", Map.of(
                        "components", List.of(),
                        "risks", List.of(),
                        "recommendations", List.of()
                ),
                "metadata", Map.of(
                        "userId", "user-123",
                        "modelVersion", "gpt-4",
                        "confidenceScore", 0.92,
                        "processingTimeMs", 1200
                )
        );

        String message = objectMapper.writeValueAsString(payload);

        when(createReportUseCase.execute(eq(diagramId), any(AIAnalysisResult.class)))
                .thenThrow(new RuntimeException("database error"));

        listener.handleReportGeneration(message);

        InOrder inOrder = inOrder(statusGateway);
        inOrder.verify(statusGateway).updateStatus(diagramId, "EM_PROCESSAMENTO");
        inOrder.verify(statusGateway).updateStatus(diagramId, "ERRO");
    }
}
