package com.fiap.report.application.impl;

import com.fiap.report.domain.report.AnalysisReport;
import com.fiap.report.gateway.AnalysisReportGateway;
import com.fiap.report.infrastructure.pdf.PdfReportGenerator;
import com.fiap.report.usecase.GenerateReportPdfUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class GenerateReportPdfUseCaseImpl implements GenerateReportPdfUseCase {

    private final AnalysisReportGateway reportGateway;
    private final PdfReportGenerator pdfReportGenerator;

    @Override
    public byte[] execute(UUID diagramId) {
        log.debug("Generating PDF for report {}", diagramId);
        AnalysisReport report = reportGateway.findByDiagramId(diagramId)
                .orElseThrow(() -> new RuntimeException("Report not found: " + diagramId));
        return pdfReportGenerator.generateReportPdf(report);
    }
}
