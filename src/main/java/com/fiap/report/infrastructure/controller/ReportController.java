package com.fiap.report.infrastructure.controller;

import com.fiap.report.domain.report.AnalysisReport;
import com.fiap.report.domain.report.ReportStatus;
import com.fiap.report.gateway.StatusGateway;
import com.fiap.report.infrastructure.mapper.AIAnalysisMapper;
import com.fiap.report.usecase.CreateReportUseCase;
import com.fiap.report.usecase.FindReportByDiagramIdUseCase;
import com.fiap.report.usecase.GetReportUseCase;
import com.fiap.report.usecase.ListReportsUseCase;
import com.fiap.report.usecase.dto.AIAnalysisResult;
import com.fiap.report.usecase.dto.ExtractedComponent;
import com.fiap.report.usecase.dto.IdentifiedRisk;
import com.fiap.report.usecase.dto.GeneratedRecommendation;
import com.fiap.report.infrastructure.dto.ProcessingStatusResponse;
import com.fiap.report.infrastructure.dto.ReportResponse;
import com.fiap.report.infrastructure.dto.ReportSummaryResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

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
    private final StatusGateway statusGateway;
    private final AIAnalysisMapper aiAnalysisMapper;

    // Endpoint de ingestão de análise da IA (harness local / simulação do payload que deveria chegar via fila)
    @PostMapping("/{uploadId}")
    public ResponseEntity<ReportResponse> generateReport(
            @PathVariable String uploadId,
            @RequestBody Map<String, Object> requestData) {

        log.info("Receiving AI analysis payload for upload: {}", uploadId);

        try {
            UUID diagramId = convertToUUID(uploadId);

            Optional<AnalysisReport> existingReport = findReportByDiagramIdUseCase.execute(diagramId);
            if (existingReport.isPresent()) {
                log.info("Report already exists for upload: {}, returning existing report: {}", uploadId, existingReport.get().getId());
                return ResponseEntity.ok(ReportResponse.from(existingReport.get()));
            }

            statusGateway.updateStatus(diagramId, "EM_PROCESSAMENTO");

            AIAnalysisResult aiResult = buildAIAnalysisResult(diagramId, requestData);
            var report = createReportUseCase.execute(diagramId, aiResult);

            statusGateway.updateStatus(diagramId, "ANALISADO");
            log.info("Report generated successfully: {} for upload: {}", report.getId(), uploadId);
            return ResponseEntity.ok(ReportResponse.from(report));

        } catch (Exception e) {
            log.error("Error generating report for upload: {}", uploadId, e);
            try {
                UUID diagramId = convertToUUID(uploadId);
                statusGateway.updateStatus(diagramId, "ERRO");
            } catch (Exception updateError) {
                log.error("Error updating status to ERRO for upload: {}", uploadId, updateError);
            }
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
            UUID diagramId = convertToUUID(uploadId);
            var report = getReportUseCase.execute(diagramId);
            return ResponseEntity.ok(ReportResponse.from(report));

        } catch (Exception e) {
            log.error("Error getting report for upload: {} - Error: {}", uploadId, e.getMessage(), e);
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{uploadId}/status")
    public ResponseEntity<ProcessingStatusResponse> getProcessingStatus(@PathVariable String uploadId) {
        log.info("Getting status for upload: {}", uploadId);

        try {
            UUID diagramId = convertToUUID(uploadId);
            var reportOptional = findReportByDiagramIdUseCase.execute(diagramId);
            if (reportOptional.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok(mapToProcessingStatusResponse(reportOptional.get()));
        } catch (Exception e) {
            log.error("Error getting processing status for upload: {} - {}", uploadId, e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    private AIAnalysisResult buildAIAnalysisResult(UUID diagramId, Map<String, Object> requestData) {
        Map<String, Object> analysis = (Map<String, Object>) requestData.get("analysis");
        Map<String, Object> metadata = (Map<String, Object>) requestData.get("metadata");

        if (analysis == null || metadata == null) {
            throw new IllegalArgumentException("Payload must include analysis and metadata sections.");
        }

        List<Map<String, Object>> componentsData = (List<Map<String, Object>>) analysis.get("components");
        List<Map<String, Object>> risksData = (List<Map<String, Object>>) analysis.get("risks");
        List<Map<String, Object>> recommendationsData = (List<Map<String, Object>>) analysis.get("recommendations");

        List<ExtractedComponent> components = componentsData == null ? List.of() : componentsData.stream()
                .map(aiAnalysisMapper::mapToComponent)
                .toList();

        List<IdentifiedRisk> risks = risksData == null ? List.of() : risksData.stream()
                .map(aiAnalysisMapper::mapToRisk)
                .toList();

        List<GeneratedRecommendation> recommendations = recommendationsData == null ? List.of() : recommendationsData.stream()
                .map(aiAnalysisMapper::mapToRecommendation)
                .toList();

        return AIAnalysisResult.builder()
                .diagramId(diagramId)
                .userId((String) metadata.getOrDefault("userId", "default-user"))
                .extractedComponents(components)
                .identifiedRisks(risks)
                .generatedRecommendations(recommendations)
                .modelVersion((String) metadata.getOrDefault("modelVersion", "unknown"))
                .confidenceScore(((Number) metadata.getOrDefault("confidenceScore", 0.0)).doubleValue())
                .processingTimeMs(((Number) metadata.getOrDefault("processingTimeMs", 0)).longValue())
                .build();
    }

    private ProcessingStatusResponse mapToProcessingStatusResponse(AnalysisReport report) {
        var status = report.getStatus();
        return ProcessingStatusResponse.builder()
                .id(report.getDiagramId())
                .status(status != null ? status.name() : "UNKNOWN")
                .progress(mapProgress(status))
                .estimatedTimeRemaining(status == null || status == ReportStatus.ANALISADO ? "0 minutos" : "Desconhecido")
                .currentStep(status != null ? status.getDisplayName() : "Status desconhecido")
                .createdAt(report.getGeneratedAt())
                .updatedAt(report.getGeneratedAt())
                .build();
    }

    private int mapProgress(ReportStatus status) {
        if (status == null) {
            return 0;
        }
        switch (status) {
            case RECEBIDO:
                return 10;
            case EM_PROCESSAMENTO:
                return 50;
            case ANALISADO:
                return 100;
            case ERRO:
            default:
                return 0;
        }
    }

    private UUID convertToUUID(String uploadId) {
        try {
            return UUID.fromString(uploadId);
        } catch (IllegalArgumentException e) {
            return UUID.nameUUIDFromBytes(uploadId.getBytes());
        }
    }

    @GetMapping("/{uploadId}/download")
    public ResponseEntity<byte[]> downloadReport(@PathVariable String uploadId) {
        log.info("Downloading report: {}", uploadId);

        try {
            UUID diagramId = convertToUUID(uploadId);
            var report = getReportUseCase.execute(diagramId);

            String templateId = report.getTemplateId() != null ? report.getTemplateId().toLowerCase() : "template-tecnico";

            String reportContent;
            if (templateId.contains("executivo")) {
                reportContent = generateExecutivePdf(report, uploadId);
            } else if (templateId.contains("seguranca") || templateId.contains("pci")) {
                reportContent = generateSecurityPdf(report, uploadId);
            } else {
                reportContent = generateTechnicalPdf(report, uploadId);
            }

            return ResponseEntity.ok()
                    .header("Content-Type", "application/pdf")
                    .header("Content-Disposition", "attachment; filename=report-" + uploadId + ".pdf")
                    .body(reportContent.getBytes());

        } catch (Exception e) {
            log.error("Error downloading report for upload: {}", uploadId, e);
            return ResponseEntity.notFound().build();
        }
    }

    private String generateExecutivePdf(AnalysisReport report, String uploadId) {
        List<String> pdfLines = new ArrayList<>();

        pdfLines.add("Resumo Executivo:");
        pdfLines.add("- Total de Componentes Mapeados: " + (report.getComponents() != null ? report.getComponents().size() : 0));

        long criticalRisks = report.getRisks() != null ? report.getRisks().stream()
                .filter(r -> "CRITICAL".equals(r.getLevel().name())).count() : 0;
        long highRisks = report.getRisks() != null ? report.getRisks().stream()
                .filter(r -> "HIGH".equals(r.getLevel().name())).count() : 0;

        pdfLines.add("- Nivel de Risco Geral: " + (criticalRisks > 0 ? "CRITICO (Requer Atencao Imediata)" : "CONTROLADO"));
        pdfLines.add("- Total de Riscos Severos (Critico/Alto): " + (criticalRisks + highRisks));

        pdfLines.add("Principais Impactos no Negocio:");
        if (report.getRisks() != null && !report.getRisks().isEmpty() && (criticalRisks > 0 || highRisks > 0)) {
            report.getRisks().stream()
                    .filter(r -> "CRITICAL".equals(r.getLevel().name()) || "HIGH".equals(r.getLevel().name()))
                    .limit(3)
                    .forEach(risk -> pdfLines.add("- " + risk.getImpact()));
        } else {
            pdfLines.add("- Nenhum risco com impacto critico ao negocio foi identificado.");
            pdfLines.add("- Recomendacao: Arquitetura liberada para proximas fases.");
        }

        return createRawPdfString(
                "Relatorio EXECUTIVO de Arquitetura",
                uploadId,
                pdfLines.toArray(new String[0])
        );
    }

    private String generateSecurityPdf(AnalysisReport report, String uploadId) {
        List<String> pdfLines = new ArrayList<>();

        pdfLines.add("Status de Conformidade:");
        pdfLines.add("- Template Aplicado: Regras de Seguranca Estrita");

        boolean hasCritical = report.getRisks() != null && report.getRisks().stream()
                .anyMatch(r -> "CRITICAL".equals(r.getLevel().name()));
        pdfLines.add("- Avaliacao de Vulnerabilidade: " + (hasCritical ? "CRITICO" : "ALTO"));

        pdfLines.add("Vulnerabilidades Detectadas:");
        if (report.getRisks() != null && !report.getRisks().isEmpty()) {
            report.getRisks().stream()
                    .filter(r -> "CRITICAL".equals(r.getLevel().name()) || "HIGH".equals(r.getLevel().name()))
                    .forEach(risk -> pdfLines.add("- [" + risk.getLevel().name() + "] " + risk.getDescription()));
        } else {
            pdfLines.add("- Nenhum risco critico ou alto detectado.");
        }

        pdfLines.add("Plano de Acao Recomendado (SecOps):");
        if (report.getRisks() != null) {
            report.getRisks().stream()
                    .filter(r -> r.getMitigation() != null && !r.getMitigation().isEmpty())
                    .flatMap(r -> r.getMitigation().stream())
                    .distinct()
                    .limit(4)
                    .forEach(mitigation -> pdfLines.add("- " + mitigation));
        }

        String[] linesArray = pdfLines.toArray(new String[0]);

        return createRawPdfString(
                "Auditoria de SEGURANCA e Compliance",
                uploadId,
                linesArray
        );
    }

    private String generateTechnicalPdf(AnalysisReport report, String uploadId) {
        List<String> pdfLines = new ArrayList<>();

        pdfLines.add("Inventario de Componentes Mapeados:");
        if (report.getComponents() != null && !report.getComponents().isEmpty()) {
            report.getComponents().forEach(comp ->
                    pdfLines.add("- " + comp.getName() + " (" + comp.getType().name() + ")"));
        } else {
            pdfLines.add("- Nenhum componente identificado no diagrama.");
        }

        pdfLines.add("Riscos de Arquitetura Identificados:");
        if (report.getRisks() != null && !report.getRisks().isEmpty()) {
            report.getRisks().stream()
                    .limit(3)
                    .forEach(risk -> pdfLines.add("- " + risk.getDescription() + " (Impacto: " + risk.getImpact() + ")"));
        } else {
            pdfLines.add("- Nenhum risco arquitetural listado.");
        }

        pdfLines.add("Recomendacoes de Engenharia:");
        if (report.getRecommendations() != null && !report.getRecommendations().isEmpty()) {
            report.getRecommendations().stream()
                    .limit(3)
                    .forEach(rec -> pdfLines.add("- " + rec.getDescription() + " [Prioridade: " + rec.getPriority().name() + "]"));
        } else {
            pdfLines.add("- Nenhuma recomendacao gerada para esta estrutura.");
        }

        String[] linesArray = pdfLines.toArray(new String[0]);

        return createRawPdfString(
                "Analise TECNICA Detalhada (Deep Dive)",
                uploadId,
                linesArray
        );
    }

    // Helper dinâmico para gerar a estrutura base do arquivo PDF cru (PDF-1.1)
    private String createRawPdfString(String title, String uploadId, String... lines) {
        StringBuilder streamContent = new StringBuilder();
        streamContent.append("BT /F1 14 Tf 50 720 Td (").append(title).append(") Tj ET\n");
        streamContent.append("BT /F1 10 Tf 50 690 Td (Upload ID: ").append(uploadId).append(") Tj ET\n");
        streamContent.append("BT /F1 10 Tf 50 670 Td (Data: ").append(java.time.LocalDate.now()).append(") Tj ET\n");

        int yPosition = 630;
        for (String line : lines) {
            int fontSize = line.endsWith(":") ? 12 : 10;
            streamContent.append("BT /F1 ").append(fontSize).append(" Tf 50 ").append(yPosition).append(" Td (").append(line).append(") Tj ET\n");

            yPosition -= 20;
            if (line.endsWith(":")) yPosition -= 5;
        }

        String streamStr = streamContent.toString();

        return "%PDF-1.1\n" +
                "1 0 obj<< /Type /Catalog /Pages 2 0 R >>endobj\n" +
                "2 0 obj<< /Type /Pages /Kids [3 0 R] /Count 1 >>endobj\n" +
                "3 0 obj<< /Type /Page /Parent 2 0 R /Resources << /Font << /F1 4 0 R >> >> /MediaBox [0 0 612 792] /Contents 5 0 R >>endobj\n" +
                "4 0 obj<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica >>endobj\n" +
                "5 0 obj<< /Length " + streamStr.length() + " >>stream\n" +
                streamStr +
                "endstream endobj\n" +
                "xref\n0 6\n0000000000 65535 f \n0000000010 00000 n \n0000000079 00000 n \n0000000173 00000 n \n0000000301 00000 n \n0000000380 00000 n \n" +
                "trailer<< /Size 6 /Root 1 0 R >>\nstartxref\n650\n%%EOF";
    }
}
