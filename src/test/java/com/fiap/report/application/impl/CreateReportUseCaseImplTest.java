package com.fiap.report.application.impl;

import com.fiap.report.domain.report.AnalysisReport;
import com.fiap.report.gateway.AnalysisReportGateway;
import com.fiap.report.usecase.dto.AIAnalysisResult;
import com.fiap.report.usecase.dto.ExtractedComponent;
import com.fiap.report.usecase.dto.GeneratedRecommendation;
import com.fiap.report.usecase.dto.IdentifiedRisk;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CreateReportUseCaseImplTest {

    private AnalysisReportGateway repository;
    private CreateReportUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        repository = mock(AnalysisReportGateway.class);
        useCase = new CreateReportUseCaseImpl(repository);
    }

    @Test
    void execute_happyPath_savesReport() {
        UUID diagramId = UUID.randomUUID();

        AIAnalysisResult ai = AIAnalysisResult.builder()
                .diagramId(diagramId)
                .extractedComponents(List.of(ExtractedComponent.builder()
                        .name("API Gateway")
                        .type("API")
                        .connections(List.of("user-service"))
                        .properties(java.util.Map.of("protocol","HTTP"))
                        .technology("Spring Boot")
                        .description("Gateway")
                        .build()))
                .identifiedRisks(List.of(IdentifiedRisk.builder()
                        .id("r1")
                        .description("fail")
                        .level("HIGH")
                        .category("RELIABILITY")
                        .affectedComponent("comp-1")
                        .mitigation(List.of("m"))
                        .severityScore(5)
                        .impact("imp")
                        .build()))
                .generatedRecommendations(List.of(GeneratedRecommendation.builder()
                        .id("g1")
                        .description("rec")
                        .type("PERFORMANCE")
                        .priority("MEDIUM")
                        .effort("LOW")
                        .rationale("r")
                        .targetComponent("comp-1")
                        .steps(List.of("s1"))
                        .build()))
                .build();

        AnalysisReport saved = AnalysisReport.create(diagramId, "user1");
        when(repository.save(any())).thenReturn(saved);

        // capture the report passed to repository.save to ensure status was set to ANALISADO
        AnalysisReport result = useCase.execute(diagramId, ai);

        assertThat(result).isNotNull();
        assertThat(result.getDiagramId()).isEqualTo(diagramId);

        // capture argument passed to repository.save and verify status was set to ANALISADO
        org.mockito.ArgumentCaptor<AnalysisReport> captor = org.mockito.ArgumentCaptor.forClass(AnalysisReport.class);
        org.mockito.Mockito.verify(repository).save(captor.capture());
        AnalysisReport passed = captor.getValue();
        assertThat(passed.getStatus()).isEqualTo(com.fiap.report.domain.report.ReportStatus.ANALISADO);
    }

    @Test
    void execute_repositoryThrowsException_wrapsInRuntimeException() {
        UUID diagramId = UUID.randomUUID();

        AIAnalysisResult ai = AIAnalysisResult.builder()
                .diagramId(diagramId)
                .extractedComponents(List.of())
                .identifiedRisks(List.of())
                .generatedRecommendations(List.of())
                .build();

        when(repository.save(any())).thenThrow(new RuntimeException("database error"));

        assertThatThrownBy(() -> useCase.execute(diagramId, ai))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Failed to create report");
    }

    @Test
    void execute_invalidComponentType_throwsException() {
        UUID diagramId = UUID.randomUUID();

        AIAnalysisResult ai = AIAnalysisResult.builder()
                .diagramId(diagramId)
                .extractedComponents(List.of(ExtractedComponent.builder()
                        .name("Invalid Component")
                        .type("INVALID_TYPE")
                        .connections(List.of())
                        .properties(java.util.Map.of())
                        .technology("Test")
                        .description("Test")
                        .build()))
                .identifiedRisks(List.of())
                .generatedRecommendations(List.of())
                .build();

        assertThatThrownBy(() -> useCase.execute(diagramId, ai))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Failed to create report");
    }
}
