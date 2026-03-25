package com.fiap.report.infrastructure.controller;

import com.fiap.report.gateway.AnalysisReportGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportDeleteController {

    private final AnalysisReportGateway reportGateway;

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReport(@PathVariable UUID id) {
        log.info("Deleting report: {}", id);

        if (!reportGateway.existsById(id)) {
            throw new RuntimeException("Report not found: " + id);
        }

        reportGateway.deleteById(id);
        log.info("Report deleted successfully: {}", id);

        return ResponseEntity.noContent().build();
    }
}
