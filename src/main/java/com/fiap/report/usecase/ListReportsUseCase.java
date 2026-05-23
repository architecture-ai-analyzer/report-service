package com.fiap.report.usecase;

import com.fiap.report.domain.report.AnalysisReport;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ListReportsUseCase {
    Page<AnalysisReport> execute(String userId, Pageable pageable);
}
