package com.fiap.report.gateway;

import com.fiap.report.domain.report.AnalysisReport;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AnalysisReportGateway {
    AnalysisReport save(AnalysisReport report);
    Optional<AnalysisReport> findById(UUID id);
    Optional<AnalysisReport> findByDiagramId(UUID diagramId);
    List<AnalysisReport> findAll();
    boolean existsById(UUID id);
    void deleteById(UUID id);
}
