package com.fiap.report.infrastructure.repository;

import com.fiap.report.domain.component.ComponentData;
import com.fiap.report.domain.component.ComponentType;
import com.fiap.report.domain.recommendation.Effort;
import com.fiap.report.domain.recommendation.Priority;
import com.fiap.report.domain.recommendation.RecommendationData;
import com.fiap.report.domain.recommendation.RecommendationType;
import com.fiap.report.domain.report.AnalysisReport;
import com.fiap.report.domain.report.ReportStatus;
import com.fiap.report.domain.risk.RiskCategory;
import com.fiap.report.domain.risk.RiskData;
import com.fiap.report.domain.risk.RiskLevel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class AnalysisReportEntityTest {

    private AnalysisReport domainReport;

    @BeforeEach
    void setUp() {
        UUID diagramId = UUID.randomUUID();
        domainReport = AnalysisReport.builder()
                .id(UUID.randomUUID())
                .diagramId(diagramId)
                .components(List.of(
                        ComponentData.builder()
                                .id("comp-1")
                                .name("API Gateway")
                                .type(ComponentType.API)
                                .build()
                ))
                .risks(List.of(
                        RiskData.builder()
                                .id("risk-1")
                                .description("Single point of failure")
                                .level(RiskLevel.HIGH)
                                .category(RiskCategory.RELIABILITY)
                                .build()
                ))
                .recommendations(List.of(
                        RecommendationData.builder()
                                .id("rec-1")
                                .description("Add caching")
                                .type(RecommendationType.PERFORMANCE)
                                .priority(Priority.HIGH)
                                .effort(Effort.MEDIUM)
                                .build()
                ))
                .generatedAt(LocalDateTime.now())
                .generatedBy("AI_SERVICE")
                .status(ReportStatus.ANALISADO)
                .build();
    }

    @Test
    void fromDomain_convertsDomainToEntity() {
        AnalysisReportEntity entity = AnalysisReportEntity.fromDomain(domainReport);

        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isEqualTo(domainReport.getId());
        assertThat(entity.getDiagramId()).isEqualTo(domainReport.getDiagramId());
        assertThat(entity.getStatus()).isEqualTo(domainReport.getStatus());
    }

    @Test
    void toDomain_convertsEntityToDomain() {
        AnalysisReportEntity entity = AnalysisReportEntity.fromDomain(domainReport);
        AnalysisReport converted = entity.toDomain();

        assertThat(converted).isNotNull();
        assertThat(converted.getId()).isEqualTo(entity.getId());
        assertThat(converted.getDiagramId()).isEqualTo(entity.getDiagramId());
        assertThat(converted.getStatus()).isEqualTo(entity.getStatus());
    }

    @Test
    void prePersist_setsDefaultValues() {
        AnalysisReportEntity entity = new AnalysisReportEntity();
        entity.prePersist();

        assertThat(entity.getId()).isNotNull();
        assertThat(entity.getGeneratedAt()).isNotNull();
        assertThat(entity.getGeneratedBy()).isEqualTo("AI_SERVICE");
        assertThat(entity.getStatus()).isEqualTo(ReportStatus.RECEBIDO);
    }

    @Test
    void prePersist_doesNotOverrideExistingValues() {
        UUID existingId = UUID.randomUUID();
        LocalDateTime existingTime = LocalDateTime.now().minusHours(1);
        String existingUser = "EXISTING_USER";
        ReportStatus existingStatus = ReportStatus.ANALISADO;

        AnalysisReportEntity entity = new AnalysisReportEntity();
        entity.setId(existingId);
        entity.setGeneratedAt(existingTime);
        entity.setGeneratedBy(existingUser);
        entity.setStatus(existingStatus);
        
        entity.prePersist();

        assertThat(entity.getId()).isEqualTo(existingId);
        assertThat(entity.getGeneratedAt()).isEqualTo(existingTime);
        assertThat(entity.getGeneratedBy()).isEqualTo(existingUser);
        assertThat(entity.getStatus()).isEqualTo(existingStatus);
    }
}
