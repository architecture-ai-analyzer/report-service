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
import com.fiap.report.usecase.GenerateReportPdfUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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
    private final GenerateReportPdfUseCase generateReportPdfUseCase;
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
                .extractedComponents(components)
                .identifiedRisks(risks)
                .generatedRecommendations(recommendations)
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
            byte[] reportContent = generateReportPdfUseCase.execute(diagramId);

            String filename = "report-" + uploadId + ".pdf";
            String encodedFilename = URLEncoder.encode(filename, StandardCharsets.UTF_8);

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + filename + "\"; filename*=UTF-8''" + encodedFilename)
                    .body(reportContent);

        } catch (Exception e) {
            log.error("Error downloading report for upload: {}", uploadId, e);
            return ResponseEntity.notFound().build();
        }
    }
}
