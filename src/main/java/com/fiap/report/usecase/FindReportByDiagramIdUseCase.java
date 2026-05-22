package com.fiap.report.usecase;

import java.util.Optional;
import java.util.UUID;

public interface FindReportByDiagramIdUseCase {
    Optional<com.fiap.report.domain.report.AnalysisReport> execute(UUID diagramId);
}
