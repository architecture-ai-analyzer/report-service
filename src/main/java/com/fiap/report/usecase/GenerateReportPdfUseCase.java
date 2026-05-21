package com.fiap.report.usecase;

import java.util.UUID;

public interface GenerateReportPdfUseCase {
    byte[] execute(UUID diagramId);
}
