package com.fiap.report.infrastructure.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiap.report.gateway.StatusGateway;
import com.fiap.report.usecase.CreateReportUseCase;
import com.fiap.report.usecase.dto.AIAnalysisResult;
import com.fiap.report.usecase.dto.ExtractedComponent;
import com.fiap.report.usecase.dto.IdentifiedRisk;
import com.fiap.report.usecase.dto.GeneratedRecommendation;
import io.awspring.cloud.sqs.annotation.SqsListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "aws.access.key")
public class ReportGenerationListener {

    private final CreateReportUseCase createReportUseCase;
    private final StatusGateway statusGateway;
    private final ObjectMapper objectMapper;

    @Value("${aws.sqs.report-generation-queue:report-generation-queue}")
    private String reportGenerationQueue;

    /**
     * Processa mensagem da fila SQS com resultado da análise da IA
     * Formato esperado:
     * {
     *   "uploadId": "arch-analysis-2024-03-30-001",
     *   "analysis": {
     *     "components": [...],
     *     "risks": [...],
     *     "recommendations": [...]
     *   },
     *   "metadata": {
     *     "modelVersion": "gpt-4-vision-preview",
     *     "confidenceScore": 0.89,
     *     "processingTimeMs": 3200,
     *     "userId": "user-123"
     *   }
     * }
     */
    @SqsListener("${aws.sqs.report-generation-queue:report-generation-queue}")
    public void handleReportGeneration(String message) {
        log.info("Received report generation message from queue {}: {}", reportGenerationQueue, message);

        try {
            // Parse da mensagem da IA
            Map<String, Object> messageData = objectMapper.readValue(message, Map.class);
            
            String uploadId = (String) messageData.get("uploadId");
            Map<String, Object> analysis = (Map<String, Object>) messageData.get("analysis");
            Map<String, Object> metadata = (Map<String, Object>) messageData.get("metadata");
            
            // Converter para diagramId UUID
            UUID diagramId = convertToUUID(uploadId);
            
            // 1. Atualizar status para PROCESSING
            statusGateway.updateStatus(diagramId, "PROCESSING");
            log.info("Status updated to PROCESSING for diagram: {}", diagramId);
            
            // Extrair componentes
            List<Map<String, Object>> componentsData = (List<Map<String, Object>>) analysis.get("components");
            List<ExtractedComponent> components = componentsData.stream()
                    .map(this::mapToComponent)
                    .toList();
            
            // Extrair riscos
            List<Map<String, Object>> risksData = (List<Map<String, Object>>) analysis.get("risks");
            List<IdentifiedRisk> risks = risksData.stream()
                    .map(this::mapToRisk)
                    .toList();
            
            // Extrair recomendações
            List<Map<String, Object>> recommendationsData = (List<Map<String, Object>>) analysis.get("recommendations");
            List<GeneratedRecommendation> recommendations = recommendationsData.stream()
                    .map(this::mapToRecommendation)
                    .toList();
            
            // Criar AIAnalysisResult
            AIAnalysisResult aiResult = AIAnalysisResult.builder()
                    .diagramId(diagramId)
                    .userId((String) metadata.getOrDefault("userId", "system"))
                    .extractedComponents(components)
                    .identifiedRisks(risks)
                    .generatedRecommendations(recommendations)
                    .modelVersion((String) metadata.getOrDefault("modelVersion", "unknown"))
                    .confidenceScore(((Number) metadata.getOrDefault("confidenceScore", 0.0)).doubleValue())
                    .processingTimeMs(((Number) metadata.getOrDefault("processingTimeMs", 0)).longValue())
                    .build();
            
            // Criar relatório
            var report = createReportUseCase.execute(diagramId, aiResult);
            log.info("Report created successfully: {} for uploadId: {}", report.getId(), uploadId);
            
            // 2. Atualizar status para COMPLETED
            statusGateway.updateStatus(diagramId, "COMPLETED");
            log.info("Status updated to COMPLETED for diagram: {}", diagramId);
            
        } catch (Exception e) {
            log.error("Error processing report generation message from queue {}", reportGenerationQueue, e);
            
            // 3. Em caso de erro, atualizar status para FAILED
            try {
                UUID diagramId = extractDiagramIdFromMessage(message);
                statusGateway.updateStatus(diagramId, "FAILED");
                log.info("Status updated to FAILED for diagram: {}", diagramId);
            } catch (Exception statusError) {
                log.error("Failed to update status to FAILED", statusError);
            }
            
            throw new RuntimeException("Failed to process report generation", e);
        }
    }
    
    private UUID extractDiagramIdFromMessage(String message) {
        try {
            Map<String, Object> messageData = objectMapper.readValue(message, Map.class);
            String uploadId = (String) messageData.get("uploadId");
            return convertToUUID(uploadId);
        } catch (Exception e) {
            log.error("Failed to extract diagramId from message: {}", message, e);
            return UUID.randomUUID(); // Fallback
        }
    }
    
    private UUID convertToUUID(String uploadId) {
        try {
            return UUID.fromString(uploadId);
        } catch (IllegalArgumentException e) {
            // Se não for UUID, criar um consistente baseado na string
            return UUID.nameUUIDFromBytes(uploadId.getBytes());
        }
    }
    
    @SuppressWarnings("unchecked")
    private ExtractedComponent mapToComponent(Map<String, Object> componentData) {
        Map<String, Object> properties = (Map<String, Object>) componentData.get("properties");
        List<String> connections = (List<String>) componentData.get("connections");
        
        return ExtractedComponent.builder()
                .id((String) componentData.get("id"))
                .name((String) componentData.get("name"))
                .type((String) componentData.get("type"))
                .connections(connections)
                .properties(properties)
                .technology((String) componentData.get("technology"))
                .description((String) componentData.get("description"))
                .build();
    }
    
    @SuppressWarnings("unchecked")
    private IdentifiedRisk mapToRisk(Map<String, Object> riskData) {
        List<String> mitigation = (List<String>) riskData.get("mitigation");
        
        return IdentifiedRisk.builder()
                .id((String) riskData.get("id"))
                .description((String) riskData.get("description"))
                .level((String) riskData.get("level"))
                .affectedComponent((String) riskData.get("affectedComponent"))
                .category((String) riskData.get("category"))
                .mitigation(mitigation)
                .severityScore(((Number) riskData.getOrDefault("severityScore", 5)).intValue())
                .impact((String) riskData.get("impact"))
                .build();
    }
    
    @SuppressWarnings("unchecked")
    private GeneratedRecommendation mapToRecommendation(Map<String, Object> recommendationData) {
        List<String> steps = (List<String>) recommendationData.get("steps");
        
        return GeneratedRecommendation.builder()
                .id((String) recommendationData.get("id"))
                .description((String) recommendationData.get("description"))
                .targetComponent((String) recommendationData.get("targetComponent"))
                .type((String) recommendationData.get("type"))
                .priority((String) recommendationData.get("priority"))
                .rationale((String) recommendationData.get("rationale"))
                .effort((String) recommendationData.get("effort"))
                .steps(steps)
                .build();
    }
}
