package com.fiap.report.application.impl;

import com.fiap.report.gateway.AnalysisReportGateway;
import com.fiap.report.usecase.GetReportUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class GetReportUseCaseImpl implements GetReportUseCase {

    private final AnalysisReportGateway repository;

    @Override
    public com.fiap.report.domain.report.AnalysisReport execute(UUID diagramId) {
        log.debug("Getting report by diagram ID: {}", diagramId);
        return repository.findByDiagramId(diagramId)
                .orElseThrow(() -> new RuntimeException("Report not found: " + diagramId));
    }
}
