package com.fiap.report.application.impl;

import com.fiap.report.domain.report.AnalysisReport;
import com.fiap.report.gateway.AnalysisReportGateway;
import com.fiap.report.infrastructure.pdf.PdfReportGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GenerateReportPdfUseCaseImplTest {

    private AnalysisReportGateway reportGateway;
    private PdfReportGenerator pdfReportGenerator;
    private GenerateReportPdfUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        reportGateway = mock(AnalysisReportGateway.class);
        pdfReportGenerator = mock(PdfReportGenerator.class);
        useCase = new GenerateReportPdfUseCaseImpl(reportGateway, pdfReportGenerator);
    }

    @Test
    void execute_happyPath_generatesPdf() {
        UUID diagramId = UUID.randomUUID();
        AnalysisReport report = AnalysisReport.builder()
                .id(UUID.randomUUID())
                .diagramId(diagramId)
                .templateId("technical")
                .build();

        byte[] pdfBytes = "PDF content".getBytes();

        when(reportGateway.findByDiagramId(diagramId)).thenReturn(Optional.of(report));
        when(pdfReportGenerator.generateReportPdf(any(AnalysisReport.class))).thenReturn(pdfBytes);

        byte[] result = useCase.execute(diagramId);

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(pdfBytes);
    }

    @Test
    void execute_reportNotFound_throwsException() {
        UUID diagramId = UUID.randomUUID();

        when(reportGateway.findByDiagramId(diagramId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(diagramId))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Report not found");
    }
}
