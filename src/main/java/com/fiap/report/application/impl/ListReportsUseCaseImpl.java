package com.fiap.report.application.impl;

import com.fiap.report.gateway.AnalysisReportGateway;
import com.fiap.report.usecase.ListReportsUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ListReportsUseCaseImpl implements ListReportsUseCase {

    private final AnalysisReportGateway repository;

    @Override
    public Page<com.fiap.report.domain.report.AnalysisReport> execute(String userId, Pageable pageable) {
        log.debug("Listing reports for user: {} - page: {}", userId, pageable);
        
        // Por enquanto, retorna todos os relatórios (sem filtro por usuário)
        List<com.fiap.report.domain.report.AnalysisReport> allReports = repository.findAll();
        
        // TODO: Implementar paginação real e filtro por usuário
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), allReports.size());
        List<com.fiap.report.domain.report.AnalysisReport> pageContent = allReports.subList(start, end);
        
        return new PageImpl<>(pageContent, pageable, allReports.size());
    }
}
