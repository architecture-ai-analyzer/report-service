package com.fiap.report.infrastructure.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiap.report.gateway.StatusGateway;
import com.fiap.report.infrastructure.mapper.AIAnalysisMapper;
import com.fiap.report.usecase.CreateReportUseCase;
import com.fiap.report.usecase.dto.AIAnalysisResult;
import com.fiap.report.usecase.dto.ExtractedComponent;
import com.fiap.report.usecase.dto.IdentifiedRisk;
import com.fiap.report.usecase.dto.GeneratedRecommendation;
import io.awspring.cloud.sqs.annotation.SqsListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReportGenerationListener {

    private final CreateReportUseCase createReportUseCase;
    private final StatusGateway statusGateway;
    private final ObjectMapper objectMapper;
    private final AIAnalysisMapper aiAnalysisMapper;

    @Value("${aws.sqs.report-generation-queue:report-generation-queue}")
    private String reportGenerationQueue;

    @SqsListener("${aws.sqs.report-generation-queue:report-generation-queue}")
    public void handleReportGeneration(String message) {
        log.info("Received report generation message from queue {}: {}", reportGenerationQueue, message);
        try {
            Map<String, Object> messageData = objectMapper.readValue(message, Map.class);

            String uploadId = (String) messageData.get("uploadId");
            Map<String, Object> analysis = (Map<String, Object>) messageData.get("analysis");
            UUID diagramId = convertToUUID(uploadId);

            statusGateway.updateStatus(diagramId, "EM_PROCESSAMENTO");
            log.info("Status updated to EM_PROCESSAMENTO for diagram: {}", diagramId);

            List<Map<String, Object>> componentsData = (List<Map<String, Object>>) analysis.get("components");
            List<ExtractedComponent> components = componentsData.stream()
                    .map(aiAnalysisMapper::mapToComponent)
                    .toList();

            List<Map<String, Object>> risksData = (List<Map<String, Object>>) analysis.get("risks");
            List<IdentifiedRisk> risks = risksData.stream()
                    .map(aiAnalysisMapper::mapToRisk)
                    .toList();

            List<Map<String, Object>> recommendationsData = (List<Map<String, Object>>) analysis.get("recommendations");
            List<GeneratedRecommendation> recommendations = recommendationsData.stream()
                    .map(aiAnalysisMapper::mapToRecommendation)
                    .toList();

            AIAnalysisResult aiResult = AIAnalysisResult.builder()
                    .diagramId(diagramId)
                    .extractedComponents(components)
                    .identifiedRisks(risks)
                    .generatedRecommendations(recommendations)
                    .build();

            var report = createReportUseCase.execute(diagramId, aiResult);
            log.info("Report created successfully: {} for uploadId: {}", report.getId(), uploadId);

            statusGateway.updateStatus(diagramId, "ANALISADO");
            log.info("Status updated to ANALISADO for diagram: {}", diagramId);

        } catch (Exception e) {
            log.error("Error processing report generation message from queue {}", reportGenerationQueue, e);
            try {
                UUID diagramId = extractDiagramIdFromMessage(message);
                statusGateway.updateStatus(diagramId, "ERRO");
                log.info("Status updated to ERRO for diagram: {}", diagramId);
            } catch (Exception statusError) {
                log.error("Failed to update status to ERRO", statusError);
            }
            throw new RuntimeException("Failed to process message, throwing to trigger SQS retry/DLQ", e);
        }
    }

    private UUID extractDiagramIdFromMessage(String message) {
        try {
            Map<String, Object> messageData = objectMapper.readValue(message, Map.class);
            String uploadId = (String) messageData.get("uploadId");
            return convertToUUID(uploadId);
        } catch (Exception e) {
            log.error("Failed to extract diagramId from message: {}", message, e);
            return UUID.randomUUID();
        }
    }

    private UUID convertToUUID(String uploadId) {
        try {
            return UUID.fromString(uploadId);
        } catch (IllegalArgumentException e) {
            return UUID.nameUUIDFromBytes(uploadId.getBytes());
        }
    }
}