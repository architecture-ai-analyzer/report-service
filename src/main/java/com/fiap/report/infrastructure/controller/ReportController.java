package com.fiap.report.infrastructure.controller;

import com.fiap.report.gateway.AnalysisReportGateway;
import com.fiap.report.infrastructure.dto.ReportResponse;
import com.fiap.report.infrastructure.dto.ReportSummaryResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final AnalysisReportGateway repository;

    @GetMapping("/{id}")
    public ResponseEntity<ReportResponse> getReport(@PathVariable UUID id) {
        log.info("Getting report by ID: {}", id);

        var report = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Report not found: " + id));

        return ResponseEntity.ok(ReportResponse.from(report));
    }

    @GetMapping("/diagram/{diagramId}")
    public ResponseEntity<ReportResponse> getReportByDiagramId(@PathVariable UUID diagramId) {
        log.info("Getting report by diagram ID: {}", diagramId);

        var report = repository.findByDiagramId(diagramId)
                .orElseThrow(() -> new RuntimeException("Report not found for diagram: " + diagramId));

        return ResponseEntity.ok(ReportResponse.from(report));
    }

    @GetMapping("/{id}/summary")
    public ResponseEntity<ReportSummaryResponse> getReportSummary(@PathVariable UUID id) {
        log.info("Getting report summary for ID: {}", id);

        var report = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Report not found: " + id));

        ReportSummaryResponse summary = ReportSummaryResponse.builder()
                .id(report.getId())
                .diagramId(report.getDiagramId())
                .userId(report.getUserId())
                .status(report.getStatus().name())
                .createdAt(report.getGeneratedAt())
                .updatedAt(report.getGeneratedAt())
                .build();

        return ResponseEntity.ok(summary);
    }
}
