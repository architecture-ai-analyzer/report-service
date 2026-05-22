package com.fiap.report.infrastructure.controller;

import com.fiap.report.gateway.AnalysisReportGateway;
import com.fiap.report.infrastructure.config.observability.TraceSupport;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

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
        TraceSupport.tagActiveSpan("operation.type", "deleteReport");
        TraceSupport.tagActiveSpan("report.id", id.toString());

        if (!reportGateway.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Report not found: " + id);
        }

        reportGateway.deleteById(id);
        log.info("Report deleted successfully: {}", id);

        return ResponseEntity.noContent().build();
    }
}
