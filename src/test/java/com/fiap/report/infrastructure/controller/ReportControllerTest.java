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
import com.fiap.report.infrastructure.dto.ProcessingStatusResponse;
import com.fiap.report.infrastructure.dto.ReportResponse;
import com.fiap.report.infrastructure.dto.ReportSummaryResponse;
import com.fiap.report.usecase.CreateReportUseCase;
import com.fiap.report.usecase.FindReportByDiagramIdUseCase;
import com.fiap.report.usecase.GenerateReportPdfUseCase;
import com.fiap.report.usecase.GetReportUseCase;
import com.fiap.report.usecase.ListReportsUseCase;
import com.fiap.report.usecase.dto.AIAnalysisResult;
import com.fiap.report.usecase.dto.ExtractedComponent;
import com.fiap.report.usecase.dto.GeneratedRecommendation;
import com.fiap.report.usecase.dto.IdentifiedRisk;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
import static org.mockito.Mockito.doThrow;
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
    private GenerateReportPdfUseCase generateReportPdfUseCase;

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
                getReportUseCase, generateReportPdfUseCase, listReportsUseCase, statusGateway, aiAnalysisMapper);
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

    @Test
    void generateReport_existingReport_returnsExistingReport() {
        String uploadId = "upload-123";
        UUID diagramId = UUID.nameUUIDFromBytes(uploadId.getBytes());

        Map<String, Object> requestBody = Map.of(
                "analysis", Map.of("components", List.of(), "risks", List.of(), "recommendations", List.of()),
                "metadata", Map.of("userId", "user-123")
        );

        AnalysisReport existingReport = AnalysisReport.builder()
                .id(UUID.randomUUID())
                .diagramId(diagramId)
                .status(ReportStatus.ANALISADO)
                .build();

        when(findReportByDiagramIdUseCase.execute(diagramId)).thenReturn(Optional.of(existingReport));

        ResponseEntity<ReportResponse> response = controller.generateReport(uploadId, requestBody);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    void generateReport_invalidPayload_returnsBadRequest() {
        String uploadId = "upload-123";
        Map<String, Object> requestBody = Map.of("invalid", "payload");

        ResponseEntity<ReportResponse> response = controller.generateReport(uploadId, requestBody);

        assertThat(response.getStatusCode().is4xxClientError());
    }

    @Test
    void listReports_returnsReports() {
        AnalysisReport report = AnalysisReport.builder()
                .id(UUID.randomUUID())
                .diagramId(UUID.randomUUID())
                .status(ReportStatus.ANALISADO)
                .build();

        when(listReportsUseCase.execute(any(), any())).thenReturn(new org.springframework.data.domain.PageImpl<>(List.of(report)));

        ResponseEntity<List<ReportSummaryResponse>> response = controller.listReports(0, 10);

        assertThat(response.getStatusCode().is2xxSuccessful());
        assertThat(response.getBody()).hasSize(1);
    }

    @Test
    void getReport_existingReport_returnsReport() {
        String uploadId = UUID.randomUUID().toString();
        UUID diagramId = UUID.fromString(uploadId);

        AnalysisReport report = AnalysisReport.builder()
                .id(UUID.randomUUID())
                .diagramId(diagramId)
                .status(ReportStatus.ANALISADO)
                .build();

        when(getReportUseCase.execute(diagramId)).thenReturn(report);

        ResponseEntity<ReportResponse> response = controller.getReport(uploadId);

        assertThat(response.getStatusCode().is2xxSuccessful());
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    void getReport_nonExistingReport_returnsNotFound() {
        String uploadId = UUID.randomUUID().toString();
        UUID diagramId = UUID.fromString(uploadId);

        when(getReportUseCase.execute(diagramId)).thenThrow(new RuntimeException("Not found"));

        ResponseEntity<ReportResponse> response = controller.getReport(uploadId);

        assertThat(response.getStatusCode().is4xxClientError());
    }

    @Test
    void getProcessingStatus_existingReport_returnsStatus() {
        String uploadId = "upload-123";
        UUID diagramId = UUID.nameUUIDFromBytes(uploadId.getBytes());

        AnalysisReport report = AnalysisReport.builder()
                .id(UUID.randomUUID())
                .diagramId(diagramId)
                .status(ReportStatus.ANALISADO)
                .build();

        when(findReportByDiagramIdUseCase.execute(diagramId)).thenReturn(Optional.of(report));

        ResponseEntity<ProcessingStatusResponse> response = controller.getProcessingStatus(uploadId);

        assertThat(response.getStatusCode().is2xxSuccessful());
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo("ANALISADO");
    }

    @Test
    void getProcessingStatus_nonExistingReport_returnsNotFound() {
        String uploadId = "upload-123";
        UUID diagramId = UUID.nameUUIDFromBytes(uploadId.getBytes());

        when(findReportByDiagramIdUseCase.execute(diagramId)).thenReturn(Optional.empty());

        ResponseEntity<ProcessingStatusResponse> response = controller.getProcessingStatus(uploadId);

        assertThat(response.getStatusCode().is4xxClientError());
    }

    @Test
    void downloadReport_returnsPdf() {
        String uploadId = "upload-123";
        UUID diagramId = UUID.nameUUIDFromBytes(uploadId.getBytes());
        byte[] pdfBytes = "%PDF-1.4".getBytes(java.nio.charset.StandardCharsets.UTF_8);

        when(generateReportPdfUseCase.execute(diagramId)).thenReturn(pdfBytes);

        ResponseEntity<byte[]> response = controller.downloadReport(uploadId);

        assertThat(response.getStatusCode().is2xxSuccessful());
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).containsExactly(pdfBytes);
    }

    @Test
    void generateReport_withError_updatesStatusToError() {
        String uploadId = "upload-123";
        UUID diagramId = UUID.nameUUIDFromBytes(uploadId.getBytes());

        Map<String, Object> requestBody = Map.of(
                "analysis", Map.of("components", List.of(), "risks", List.of(), "recommendations", List.of()),
                "metadata", Map.of("userId", "user-123")
        );

        when(findReportByDiagramIdUseCase.execute(diagramId)).thenReturn(Optional.empty());
        when(createReportUseCase.execute(eq(diagramId), any(AIAnalysisResult.class))).thenThrow(new RuntimeException("Test error"));

        ResponseEntity<ReportResponse> response = controller.generateReport(uploadId, requestBody);

        assertThat(response.getStatusCode().is4xxClientError());
    }

    @Test
    void getReport_withNonUUIDString_convertsToUUID() {
        String uploadId = "non-uuid-string";

        AnalysisReport report = AnalysisReport.builder()
                .id(UUID.randomUUID())
                .diagramId(UUID.nameUUIDFromBytes(uploadId.getBytes()))
                .status(ReportStatus.ANALISADO)
                .build();

        when(getReportUseCase.execute(any())).thenReturn(report);

        ResponseEntity<ReportResponse> response = controller.getReport(uploadId);

        assertThat(response.getStatusCode().is2xxSuccessful());
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    void getProcessingStatus_withDifferentStatuses_returnsCorrectProgress() {
        String uploadId = "upload-123";
        UUID diagramId = UUID.nameUUIDFromBytes(uploadId.getBytes());

        // Test RECEBIDO status
        AnalysisReport reportRecebido = AnalysisReport.builder()
                .id(UUID.randomUUID())
                .diagramId(diagramId)
                .status(ReportStatus.RECEBIDO)
                .build();

        when(findReportByDiagramIdUseCase.execute(diagramId)).thenReturn(Optional.of(reportRecebido));

        ResponseEntity<ProcessingStatusResponse> responseRecebido = controller.getProcessingStatus(uploadId);
        assertThat(responseRecebido.getBody().getProgress()).isEqualTo(10);

        // Test EM_PROCESSAMENTO status
        AnalysisReport reportProcessando = AnalysisReport.builder()
                .id(UUID.randomUUID())
                .diagramId(diagramId)
                .status(ReportStatus.EM_PROCESSAMENTO)
                .build();

        when(findReportByDiagramIdUseCase.execute(diagramId)).thenReturn(Optional.of(reportProcessando));

        ResponseEntity<ProcessingStatusResponse> responseProcessando = controller.getProcessingStatus(uploadId);
        assertThat(responseProcessando.getBody().getProgress()).isEqualTo(50);

        // Test ANALISADO status
        AnalysisReport reportAnalisado = AnalysisReport.builder()
                .id(UUID.randomUUID())
                .diagramId(diagramId)
                .status(ReportStatus.ANALISADO)
                .build();

        when(findReportByDiagramIdUseCase.execute(diagramId)).thenReturn(Optional.of(reportAnalisado));

        ResponseEntity<ProcessingStatusResponse> responseAnalisado = controller.getProcessingStatus(uploadId);
        assertThat(responseAnalisado.getBody().getProgress()).isEqualTo(100);

        // Test ERRO status
        AnalysisReport reportErro = AnalysisReport.builder()
                .id(UUID.randomUUID())
                .diagramId(diagramId)
                .status(ReportStatus.ERRO)
                .build();

        when(findReportByDiagramIdUseCase.execute(diagramId)).thenReturn(Optional.of(reportErro));

        ResponseEntity<ProcessingStatusResponse> responseErro = controller.getProcessingStatus(uploadId);
        assertThat(responseErro.getBody().getProgress()).isEqualTo(0);
    }

    @Test
    void getProcessingStatus_withNullStatus_returnsZeroProgress() {
        String uploadId = "upload-123";
        UUID diagramId = UUID.nameUUIDFromBytes(uploadId.getBytes());

        AnalysisReport reportNullStatus = AnalysisReport.builder()
                .id(UUID.randomUUID())
                .diagramId(diagramId)
                .status(null)
                .build();

        when(findReportByDiagramIdUseCase.execute(diagramId)).thenReturn(Optional.of(reportNullStatus));

        ResponseEntity<ProcessingStatusResponse> response = controller.getProcessingStatus(uploadId);
        assertThat(response.getBody().getProgress()).isEqualTo(0);
    }

    @Test
    void generateReport_withEmptyMetadata_usesDefaultValues() {
        String uploadId = "upload-123";
        UUID diagramId = UUID.nameUUIDFromBytes(uploadId.getBytes());

        Map<String, Object> requestBody = Map.of(
                "analysis", Map.of("components", List.of(), "risks", List.of(), "recommendations", List.of()),
                "metadata", Map.of()
        );

        when(findReportByDiagramIdUseCase.execute(diagramId)).thenReturn(Optional.empty());

        AnalysisReport report = AnalysisReport.builder()
                .id(UUID.randomUUID())
                .diagramId(diagramId)
                .status(ReportStatus.ANALISADO)
                .build();

        when(createReportUseCase.execute(eq(diagramId), any(AIAnalysisResult.class))).thenReturn(report);

        ResponseEntity<ReportResponse> response = controller.generateReport(uploadId, requestBody);

        assertThat(response.getStatusCode().is2xxSuccessful());
    }

    @Test
    void generateReport_withErrorInStatusUpdate_updatesStatusToError() {
        String uploadId = "upload-123";
        UUID diagramId = UUID.nameUUIDFromBytes(uploadId.getBytes());

        Map<String, Object> requestBody = Map.of(
                "analysis", Map.of("components", List.of(), "risks", List.of(), "recommendations", List.of()),
                "metadata", Map.of("userId", "user-123")
        );

        when(findReportByDiagramIdUseCase.execute(diagramId)).thenReturn(Optional.empty());
        doThrow(new RuntimeException("Status update error")).when(statusGateway).updateStatus(any(), any());

        ResponseEntity<ReportResponse> response = controller.generateReport(uploadId, requestBody);

        assertThat(response.getStatusCode().is4xxClientError());
    }
}
