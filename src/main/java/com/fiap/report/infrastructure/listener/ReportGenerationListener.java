package com.fiap.report.infrastructure.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiap.report.usecase.CreateReportUseCase;
import com.fiap.report.gateway.StatusGateway;
import com.fiap.report.domain.report.AnalysisReport;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReportGenerationListener {

    private final CreateReportUseCase createReportUseCase;
    private final StatusGateway statusGateway;
    private final ObjectMapper objectMapper;

    // Método para processamento manual (sem SQS por enquanto)
    public void handleReportGeneration(String message) {
        log.info("Received report generation message: {}", message);

        try {
            // TODO: Implementar processamento quando necessário
            log.info("Processing report generation (mock implementation)");
            
        } catch (Exception e) {
            log.error("Error processing report generation message", e);
            throw new RuntimeException("Failed to process report generation", e);
        }
    }
}
