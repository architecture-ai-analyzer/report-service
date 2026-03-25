package com.fiap.report.infrastructure.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiap.report.usecase.CreateReportUseCase;
import com.fiap.report.gateway.StatusGateway;
import com.fiap.report.domain.report.AnalysisReport;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReportGenerationListener {

    private final CreateReportUseCase createReportUseCase;
    private final StatusGateway statusGateway;
    private final ObjectMapper objectMapper;

    @SqsListener(value = "${aws.sqs.report-generation-queue}")
    public void handleReportGeneration(@Payload String message) {
        log.info("Received report generation message: {}", message);

        try {
            ReportGenerationMessage reportMessage = objectMapper.readValue(message, ReportGenerationMessage.class);
            
            log.info("Processing report generation for diagram: {}, user: {}", 
                    reportMessage.getDiagramId(), reportMessage.getUserId());

            // Atualizar status para PROCESSING
            statusGateway.updateStatus(reportMessage.getDiagramId(), "PROCESSING");

            // Processar o resultado da IA e criar o relatório
            AnalysisReport report = createReportUseCase.execute(
                    reportMessage.getDiagramId(), 
                    reportMessage.getAiResult()
            );

            log.info("Report generated successfully: {}", report.getId());

            // Atualizar status para COMPLETED
            statusGateway.updateStatus(reportMessage.getDiagramId(), "COMPLETED");

        } catch (Exception e) {
            log.error("Error processing report generation message", e);
            // TODO: Atualizar status para ERROR
            throw new RuntimeException("Failed to process report generation", e);
        }
    }
}
