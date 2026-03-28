package com.fiap.report.infrastructure.repository;

import com.fiap.report.gateway.AnalysisReportGateway;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Repository
public class AnalysisReportRepositoryImpl implements AnalysisReportGateway {

    private final SpringDataAnalysisReportRepository springRepository;

    public AnalysisReportRepositoryImpl(SpringDataAnalysisReportRepository springRepository) {
        this.springRepository = springRepository;
    }

    @Override
    public com.fiap.report.domain.report.AnalysisReport save(com.fiap.report.domain.report.AnalysisReport report) {
        log.debug("Saving report for diagram: {}", report.getDiagramId());
        
        var entity = AnalysisReportEntity.fromDomain(report);
        var saved = springRepository.save(entity);
        
        return saved.toDomain();
    }

    @Override
    public Optional<com.fiap.report.domain.report.AnalysisReport> findById(UUID id) {
        log.debug("Finding report by ID: {}", id);
        return springRepository.findById(id)
                .map(AnalysisReportEntity::toDomain);
    }

    @Override
    public Optional<com.fiap.report.domain.report.AnalysisReport> findByDiagramId(UUID diagramId) {
        log.debug("Finding report by diagram ID: {}", diagramId);
        return springRepository.findByDiagramId(diagramId)
                .map(AnalysisReportEntity::toDomain);
    }

    @Override
    public List<com.fiap.report.domain.report.AnalysisReport> findAll() {
        log.debug("Finding all reports");
        return springRepository.findAll()
                .stream()
                .map(AnalysisReportEntity::toDomain)
                .toList();
    }

    @Override
    public boolean existsById(UUID id) {
        log.debug("Checking if report exists: {}", id);
        return springRepository.existsById(id);
    }

    @Override
    public void deleteById(UUID id) {
        log.debug("Deleting report by ID: {}", id);
        springRepository.deleteById(id);
    }
}
