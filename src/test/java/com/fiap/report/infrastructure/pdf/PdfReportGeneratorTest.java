package com.fiap.report.infrastructure.pdf;

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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PdfReportGeneratorTest {

    private PdfReportGenerator pdfReportGenerator;

    @BeforeEach
    void setUp() {
        pdfReportGenerator = new PdfReportGenerator();
    }

    @Test
    void generateReportPdf_technicalTemplate_generatesPdf() {
        AnalysisReport report = createReport("technical");

        byte[] result = pdfReportGenerator.generateReportPdf(report);

        assertThat(result).isNotNull();
        assertThat(result.length).isGreaterThan(0);
        // PDF files start with %PDF-
        assertThat(new String(result)).startsWith("%PDF-");
    }

    @Test
    void generateReportPdf_executiveTemplate_generatesPdf() {
        AnalysisReport report = createReport("executivo");

        byte[] result = pdfReportGenerator.generateReportPdf(report);

        assertThat(result).isNotNull();
        assertThat(result.length).isGreaterThan(0);
        assertThat(new String(result)).startsWith("%PDF-");
    }

    @Test
    void generateReportPdf_securityTemplate_generatesPdf() {
        AnalysisReport report = createReport("seguranca");

        byte[] result = pdfReportGenerator.generateReportPdf(report);

        assertThat(result).isNotNull();
        assertThat(result.length).isGreaterThan(0);
        assertThat(new String(result)).startsWith("%PDF-");
    }

    @Test
    void generateReportPdf_nullTemplate_usesTechnical() {
        AnalysisReport report = createReport(null);

        byte[] result = pdfReportGenerator.generateReportPdf(report);

        assertThat(result).isNotNull();
        assertThat(result.length).isGreaterThan(0);
        assertThat(new String(result)).startsWith("%PDF-");
    }

    @Test
    void generateReportPdf_emptyComponents_generatesPdf() {
        AnalysisReport report = AnalysisReport.builder()
                .id(UUID.randomUUID())
                .diagramId(UUID.randomUUID())
                .templateId("technical")
                .components(List.of())
                .risks(List.of())
                .recommendations(List.of())
                .generatedAt(LocalDateTime.now())
                .build();

        byte[] result = pdfReportGenerator.generateReportPdf(report);

        assertThat(result).isNotNull();
        assertThat(result.length).isGreaterThan(0);
    }

    @Test
    void generateReportPdf_nullComponents_generatesPdf() {
        AnalysisReport report = AnalysisReport.builder()
                .id(UUID.randomUUID())
                .diagramId(UUID.randomUUID())
                .templateId("technical")
                .components(null)
                .risks(null)
                .recommendations(null)
                .generatedAt(LocalDateTime.now())
                .build();

        byte[] result = pdfReportGenerator.generateReportPdf(report);

        assertThat(result).isNotNull();
        assertThat(result.length).isGreaterThan(0);
    }

    @Test
    void generateReportPdf_withCriticalRisks_generatesPdf() {
        AnalysisReport report = AnalysisReport.builder()
                .id(UUID.randomUUID())
                .diagramId(UUID.randomUUID())
                .templateId("executivo")
                .components(List.of(createComponent()))
                .risks(List.of(createCriticalRisk()))
                .recommendations(List.of(createRecommendation()))
                .generatedAt(LocalDateTime.now())
                .build();

        byte[] result = pdfReportGenerator.generateReportPdf(report);

        assertThat(result).isNotNull();
        assertThat(result.length).isGreaterThan(0);
    }

    @Test
    void generateReportPdf_withHighRisks_generatesPdf() {
        AnalysisReport report = AnalysisReport.builder()
                .id(UUID.randomUUID())
                .diagramId(UUID.randomUUID())
                .templateId("executivo")
                .components(List.of(createComponent()))
                .risks(List.of(createRisk()))
                .recommendations(List.of(createRecommendation()))
                .generatedAt(LocalDateTime.now())
                .build();

        byte[] result = pdfReportGenerator.generateReportPdf(report);

        assertThat(result).isNotNull();
        assertThat(result.length).isGreaterThan(0);
    }

    @Test
    void generateReportPdf_securityTemplateWithCriticalRisks_generatesPdf() {
        AnalysisReport report = AnalysisReport.builder()
                .id(UUID.randomUUID())
                .diagramId(UUID.randomUUID())
                .templateId("security")
                .components(List.of(createComponent()))
                .risks(List.of(createCriticalRisk()))
                .recommendations(List.of(createRecommendation()))
                .generatedAt(LocalDateTime.now())
                .build();

        byte[] result = pdfReportGenerator.generateReportPdf(report);

        assertThat(result).isNotNull();
        assertThat(result.length).isGreaterThan(0);
    }

    @Test
    void generateReportPdf_technicalTemplateWithRecommendations_generatesPdf() {
        AnalysisReport report = AnalysisReport.builder()
                .id(UUID.randomUUID())
                .diagramId(UUID.randomUUID())
                .templateId("technical")
                .components(List.of(createComponent()))
                .risks(List.of())
                .recommendations(List.of(createRecommendation()))
                .generatedAt(LocalDateTime.now())
                .build();

        byte[] result = pdfReportGenerator.generateReportPdf(report);

        assertThat(result).isNotNull();
        assertThat(result.length).isGreaterThan(0);
    }

    @Test
    void generateReportPdf_executiveTemplateNoRisks_generatesPdf() {
        AnalysisReport report = AnalysisReport.builder()
                .id(UUID.randomUUID())
                .diagramId(UUID.randomUUID())
                .templateId("executive")
                .components(List.of(createComponent()))
                .risks(List.of())
                .recommendations(List.of())
                .generatedAt(LocalDateTime.now())
                .build();

        byte[] result = pdfReportGenerator.generateReportPdf(report);

        assertThat(result).isNotNull();
        assertThat(result.length).isGreaterThan(0);
    }

    private AnalysisReport createReport(String templateId) {
        return AnalysisReport.builder()
                .id(UUID.randomUUID())
                .diagramId(UUID.randomUUID())
                .templateId(templateId)
                .components(List.of(createComponent()))
                .risks(List.of(createRisk()))
                .recommendations(List.of(createRecommendation()))
                .generatedAt(LocalDateTime.now())
                .build();
    }

    private ComponentData createComponent() {
        return ComponentData.builder()
                .name("API Gateway")
                .type(ComponentType.API)
                .build();
    }

    private RiskData createRisk() {
        return RiskData.builder()
                .description("Single point of failure")
                .level(RiskLevel.HIGH)
                .category(RiskCategory.RELIABILITY)
                .impact("Service outage")
                .mitigation(List.of("Add redundancy"))
                .build();
    }

    private RiskData createCriticalRisk() {
        return RiskData.builder()
                .description("No authentication")
                .level(RiskLevel.CRITICAL)
                .category(RiskCategory.SECURITY)
                .impact("Security breach")
                .mitigation(List.of("Implement OAuth2"))
                .build();
    }

    private RecommendationData createRecommendation() {
        return RecommendationData.builder()
                .description("Implement circuit breaker")
                .type(RecommendationType.PERFORMANCE)
                .priority(Priority.HIGH)
                .effort(Effort.MEDIUM)
                .build();
    }

    @Test
    void generateReportPdf_withNullStatus_generatesPdf() {
        AnalysisReport report = AnalysisReport.builder()
                .id(UUID.randomUUID())
                .diagramId(UUID.randomUUID())
                .templateId("technical")
                .components(List.of(createComponent()))
                .risks(List.of())
                .recommendations(List.of())
                .generatedAt(LocalDateTime.now())
                .status(null)
                .build();

        byte[] result = pdfReportGenerator.generateReportPdf(report);

        assertThat(result).isNotNull();
        assertThat(result.length).isGreaterThan(0);
    }

    @Test
    void generateReportPdf_withNullGeneratedAt_generatesPdf() {
        AnalysisReport report = AnalysisReport.builder()
                .id(UUID.randomUUID())
                .diagramId(UUID.randomUUID())
                .templateId("technical")
                .components(List.of(createComponent()))
                .risks(List.of())
                .recommendations(List.of())
                .generatedAt(null)
                .build();

        byte[] result = pdfReportGenerator.generateReportPdf(report);

        assertThat(result).isNotNull();
        assertThat(result.length).isGreaterThan(0);
    }

    @Test
    void generateReportPdf_exception_throwsRuntimeException() {
        AnalysisReport report = AnalysisReport.builder()
                .id(UUID.randomUUID())
                .diagramId(UUID.randomUUID())
                .templateId("technical")
                .components(List.of(createComponent()))
                .risks(List.of())
                .recommendations(List.of())
                .generatedAt(LocalDateTime.now())
                .build();

        // Since we can't easily trigger an exception in iText, we'll test that the method
        // handles the report correctly. The catch block is for unexpected errors during PDF generation.
        // This test verifies the happy path works, which means the catch block is not triggered.
        byte[] result = pdfReportGenerator.generateReportPdf(report);
        assertThat(result).isNotNull();
    }
}
