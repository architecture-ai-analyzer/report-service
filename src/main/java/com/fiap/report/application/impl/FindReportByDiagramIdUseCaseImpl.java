package com.fiap.report.application.impl;

import com.fiap.report.gateway.AnalysisReportGateway;
import com.fiap.report.usecase.FindReportByDiagramIdUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class FindReportByDiagramIdUseCaseImpl implements FindReportByDiagramIdUseCase {

    private final AnalysisReportGateway repository;

    @Override
    public Optional<com.fiap.report.domain.report.AnalysisReport> execute(UUID diagramId) {
        log.debug("Finding report by diagram ID: {}", diagramId);
        return repository.findByDiagramId(diagramId);
    }
}
