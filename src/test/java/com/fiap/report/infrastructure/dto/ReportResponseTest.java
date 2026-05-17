package com.fiap.report.infrastructure.dto;

import com.fiap.report.domain.component.ComponentData;
import com.fiap.report.domain.component.ComponentType;
import com.fiap.report.domain.recommendation.Effort;
import com.fiap.report.domain.recommendation.Priority;
import com.fiap.report.domain.recommendation.RecommendationData;
import com.fiap.report.domain.recommendation.RecommendationType;
import com.fiap.report.domain.report.AnalysisReport;
import com.fiap.report.domain.risk.RiskCategory;
import com.fiap.report.domain.risk.RiskData;
import com.fiap.report.domain.risk.RiskLevel;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ReportResponseTest {

    @Test
    void from_buildsReportResponseWithComponentsRisksAndRecommendations() {
        ComponentData c = ComponentData.builder()
                .id("c1")
                .name("API")
                .type(ComponentType.API)
                .connections(List.of("svc"))
                .technology("Spring")
                .description("desc")
                .build();

        RiskData risk = RiskData.builder()
                .id("r1")
                .description("failure")
                .level(RiskLevel.HIGH)
                .affectedComponent("c1")
                .category(RiskCategory.SECURITY)
                .mitigation(List.of("add retries"))
                .severityScore(8)
                .impact("Availability")
                .build();

        RecommendationData rec = RecommendationData.builder()
                .id("rec1")
                .description("Improve caching")
                .targetComponent("c1")
                .type(RecommendationType.PERFORMANCE)
                .priority(Priority.HIGH)
                .rationale("reduce latency")
                .effort(Effort.MEDIUM)
                .steps(List.of("add cache"))
                .build();

        AnalysisReport report = AnalysisReport.builder()
                .id(UUID.randomUUID())
                .diagramId(UUID.randomUUID())
                .components(List.of(c))
                .risks(List.of(risk))
                .recommendations(List.of(rec))
                .build();

        ReportResponse resp = ReportResponse.from(report);

        assertThat(resp).isNotNull();
        assertThat(resp.getDetectedComponents()).hasSize(1);
        assertThat(resp.getRecommendations()).hasSize(1);
        assertThat(resp.getSecurityAnalysis()).isNotNull();
        assertThat(resp.getArchitectureAnalysis()).isNotNull();
        assertThat(resp.getPerformanceAnalysis()).isNotNull();
        assertThat(resp.getSummary()).isNotNull();
        assertThat(resp.getGeneratedAt()).isNotEmpty();
    }
}
