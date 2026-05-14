package com.fiap.report.infrastructure.controller;

import com.fiap.report.usecase.CreateReportUseCase;
import com.fiap.report.usecase.FindReportByDiagramIdUseCase;
import com.fiap.report.usecase.GetReportUseCase;
import com.fiap.report.usecase.ListReportsUseCase;
import com.fiap.report.usecase.dto.AIAnalysisResult;
import com.fiap.report.usecase.dto.ExtractedComponent;
import com.fiap.report.usecase.dto.IdentifiedRisk;
import com.fiap.report.usecase.dto.GeneratedRecommendation;
import com.fiap.report.infrastructure.dto.ReportResponse;
import com.fiap.report.infrastructure.dto.ReportSummaryResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173", "http://127.0.0.1:3000", "http://127.0.0.1:5173"})
public class ReportController {

    private final CreateReportUseCase createReportUseCase;
    private final FindReportByDiagramIdUseCase findReportByDiagramIdUseCase;
    private final GetReportUseCase getReportUseCase;
    private final ListReportsUseCase listReportsUseCase;

    @PostMapping("/{uploadId}")
    public ResponseEntity<ReportResponse> generateReport(
            @PathVariable String uploadId,
            @RequestBody Map<String, Object> requestData) {

        log.info("Generating report for upload: {}", uploadId);

        try {
            UUID diagramId;
            try {
                diagramId = UUID.fromString(uploadId);
            } catch (IllegalArgumentException e) {
                diagramId = UUID.nameUUIDFromBytes(uploadId.getBytes());
            }

            Optional<com.fiap.report.domain.report.AnalysisReport> existingReport = findReportByDiagramIdUseCase.execute(diagramId);
            if (existingReport.isPresent()) {
                log.info("Report already exists for upload: {}, returning existing report: {}", uploadId, existingReport.get().getId());
                return ResponseEntity.ok(ReportResponse.from(existingReport.get()));
            }

            AIAnalysisResult aiResult = AIAnalysisResult.builder()
                    .diagramId(diagramId)
                    .userId((String) requestData.getOrDefault("userId", "default-user"))
                    .templateId((String) requestData.get("templateId"))
                    .extractedComponents(generateMockComponents())
                    .identifiedRisks(generateMockRisks())
                    .generatedRecommendations(generateMockRecommendations())
                    .modelVersion("gpt-4-vision-preview")
                    .confidenceScore(0.92)
                    .processingTimeMs(2500L)
                    .build();

            var report = createReportUseCase.execute(diagramId, aiResult);

            log.info("Report generated successfully: {} for upload: {}", report.getId(), uploadId);
            return ResponseEntity.ok(ReportResponse.from(report));

        } catch (Exception e) {
            log.error("Error generating report for upload: {}", uploadId, e);
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<ReportSummaryResponse>> listReports(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        log.info("Listing reports - page: {}, size: {}", page, size);

        var reportsPage = listReportsUseCase.execute("default-user",
                PageRequest.of(page, size));
        List<ReportSummaryResponse> reports = reportsPage.getContent().stream()
                .map(ReportSummaryResponse::from)
                .toList();
        return ResponseEntity.ok(reports);
    }

    @GetMapping("/{uploadId}")
    public ResponseEntity<ReportResponse> getReport(@PathVariable String uploadId) {
        log.info("Getting report for upload: {}", uploadId);

        try {
            UUID diagramId;
            try {
                diagramId = UUID.fromString(uploadId);
            } catch (IllegalArgumentException e) {
                diagramId = UUID.nameUUIDFromBytes(uploadId.getBytes());
            }

            var report = getReportUseCase.execute(diagramId);
            return ResponseEntity.ok(ReportResponse.from(report));

        } catch (Exception e) {
            log.error("Error getting report for upload: {} - Error: {}", uploadId, e.getMessage(), e);
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{uploadId}/status")
    public ResponseEntity<Map<String, Object>> getProcessingStatus(@PathVariable String uploadId) {
        log.info("Getting status for upload: {}", uploadId);

        return ResponseEntity.ok(Map.of(
                "id", uploadId,
                "status", "ANALISADO",
                "progress", 100,
                "estimatedTimeRemaining", "0 minutos",
                "currentStep", "Análise concluída"
        ));
    }

    @GetMapping("/{uploadId}/download")
    public ResponseEntity<byte[]> downloadReport(@PathVariable String uploadId) {
        log.info("Downloading report: {}", uploadId);

        try {
            String reportContent = generateValidPdfContent(uploadId);

            return ResponseEntity.ok()
                    .header("Content-Type", "application/pdf")
                    .header("Content-Disposition", "attachment; filename=report-" + uploadId + ".pdf")
                    .body(reportContent.getBytes());
        } catch (Exception e) {
            log.error("Error downloading report for upload: {}", uploadId, e);
            return ResponseEntity.notFound().build();
        }
    }

    private String generateValidPdfContent(String uploadId) {
        return "%PDF-1.1\n" +
                "1 0 obj<< /Type /Catalog /Pages 2 0 R >>endobj\n" +
                "2 0 obj<< /Type /Pages /Kids [3 0 R] /Count 1 >>endobj\n" +
                "3 0 obj<< /Type /Page /Parent 2 0 R /Resources << /Font << /F1 4 0 R >> >> /MediaBox [0 0 612 792] /Contents 5 0 R >>endobj\n" +
                "4 0 obj<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica >>endobj\n" +
                "5 0 obj<< /Length 200 >>stream\n" +
                "BT /F1 12 Tf 50 700 Td (Relatorio de Arquitetura) Tj ET\n" +
                "BT /F1 10 Tf 50 680 Td (Upload ID: " + uploadId + ") Tj ET\n" +
                "BT /F1 10 Tf 50 660 Td (Data: " + java.time.LocalDate.now() + ") Tj ET\n" +
                "BT /F1 10 Tf 50 640 Td (Status: Analisado) Tj ET\n" +
                "BT /F1 10 Tf 50 600 Td (Componentes:) Tj ET\n" +
                "BT /F1 10 Tf 50 580 Td (- API Gateway) Tj ET\n" +
                "BT /F1 10 Tf 50 560 Td (- User Service) Tj ET\n" +
                "BT /F1 10 Tf 50 540 Td (- Database) Tj ET\n" +
                "BT /F1 10 Tf 50 500 Td (Riscos:) Tj ET\n" +
                "BT /F1 10 Tf 50 480 Td (- HIGH: Single point of failure) Tj ET\n" +
                "BT /F1 10 Tf 50 460 Td (- CRITICAL: No authentication) Tj ET\n" +
                "BT /F1 10 Tf 50 420 Td (Recomendacoes:) Tj ET\n" +
                "BT /F1 10 Tf 50 400 Td (- Implement circuit breaker) Tj ET\n" +
                "BT /F1 10 Tf 50 380 Td (- Add distributed tracing) Tj ET\n" +
                "endstream endobj\n" +
                "xref\n" +
                "0 6\n" +
                "0000000000 65535 f \n" +
                "0000000010 00000 n \n" +
                "0000000079 00000 n \n" +
                "0000000173 00000 n \n" +
                "0000000301 00000 n \n" +
                "0000000380 00000 n \n" +
                "trailer<< /Size 6 /Root 1 0 R >>\n" +
                "startxref\n" +
                "496\n" +
                "%%EOF";
    }

    private List<ExtractedComponent> generateMockComponents() {
        return List.of(
                ExtractedComponent.builder()
                        .id("comp-api-gateway")
                        .name("API Gateway")
                        .type("API")
                        .connections(List.of("user-service", "order-service"))
                        .properties(Map.of("protocol", "REST", "rateLimit", "1000 req/s"))
                        .technology("Spring Cloud Gateway")
                        .description("Gateway para roteamento de requisições")
                        .build(),
                ExtractedComponent.builder()
                        .id("comp-user-service")
                        .name("User Service")
                        .type("MICROSERVICE")
                        .connections(List.of("database"))
                        .properties(Map.of("port", "8081", "framework", "Spring Boot"))
                        .technology("Java Spring Boot")
                        .description("Serviço de gerenciamento de usuários")
                        .build(),
                ExtractedComponent.builder()
                        .id("comp-database")
                        .name("PostgreSQL Database")
                        .type("DATABASE")
                        .connections(List.of("user-service", "order-service"))
                        .properties(Map.of("version", "14", "maxConnections", "100"))
                        .technology("PostgreSQL")
                        .description("Banco de dados principal da aplicação")
                        .build()
        );
    }

    private List<IdentifiedRisk> generateMockRisks() {
        return List.of(
                IdentifiedRisk.builder()
                        .id("risk-1")
                        .description("Ponto único de falha no banco de dados")
                        .level("HIGH")
                        .affectedComponent("database")
                        .category("RELIABILITY")
                        .mitigation(List.of("Implementar clustering no banco de dados", "Adicionar réplicas de leitura", "Configurar failover automático"))
                        .severityScore(8)
                        .impact("Indisponibilidade do serviço se o banco falhar")
                        .build(),
                IdentifiedRisk.builder()
                        .id("risk-2")
                        .description("Sem autenticação no API Gateway")
                        .level("CRITICAL")
                        .affectedComponent("API Gateway")
                        .category("SECURITY")
                        .mitigation(List.of("Implementar OAuth 2.0", "Adicionar validação JWT", "Configurar rate limiting"))
                        .severityScore(9)
                        .impact("Acesso não autorizado aos serviços")
                        .build()
        );
    }

    private List<GeneratedRecommendation> generateMockRecommendations() {
        return List.of(
                GeneratedRecommendation.builder()
                        .id("rec-1")
                        .description("Implementar padrão circuit breaker")
                        .targetComponent("API Gateway")
                        .type("RELIABILITY")
                        .priority("HIGH")
                        .rationale("Previne falhas em cascata entre serviços")
                        .effort("MEDIUM")
                        .steps(List.of("Adicionar Hystrix ou Resilience4j", "Configurar métodos de fallback", "Configurar monitoramento e alertas"))
                        .build(),
                GeneratedRecommendation.builder()
                        .id("rec-2")
                        .description("Adicionar tracing distribuído")
                        .targetComponent("All Services")
                        .type("MONITORING")
                        .priority("MEDIUM")
                        .rationale("Melhora observabilidade e debugging")
                        .effort("LOW")
                        .steps(List.of("Integrar Zipkin ou Jaeger", "Adicionar IDs de tracing nas requisições", "Configurar logging centralizado"))
                        .build()
        );
    }
}