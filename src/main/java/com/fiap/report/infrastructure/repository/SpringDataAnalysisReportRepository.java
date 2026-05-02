package com.fiap.report.infrastructure.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpringDataAnalysisReportRepository extends JpaRepository<AnalysisReportEntity, UUID> {

    Optional<AnalysisReportEntity> findByDiagramId(UUID diagramId);

    Page<AnalysisReportEntity> findByUserId(String userId, Pageable pageable);

    boolean existsByDiagramId(UUID diagramId);
}
