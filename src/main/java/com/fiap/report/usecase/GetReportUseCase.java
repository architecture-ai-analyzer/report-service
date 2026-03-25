package com.fiap.report.usecase;

import com.fiap.report.domain.report.AnalysisReport;

import java.util.UUID;

public interface GetReportUseCase {
    AnalysisReport execute(UUID reportId);
}
