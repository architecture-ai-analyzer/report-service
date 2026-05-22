package com.fiap.report.usecase;

import com.fiap.report.domain.report.AnalysisReport;
import com.fiap.report.usecase.dto.AIAnalysisResult;

import java.util.UUID;

public interface CreateReportUseCase {
    AnalysisReport execute(UUID diagramId, AIAnalysisResult aiResult);
}
