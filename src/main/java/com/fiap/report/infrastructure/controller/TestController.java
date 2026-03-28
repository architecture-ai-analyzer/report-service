package com.fiap.report.infrastructure.controller;

import com.fiap.report.usecase.CreateReportUseCase;
import com.fiap.report.usecase.dto.AIAnalysisResult;
import com.fiap.report.usecase.dto.ExtractedComponent;
import com.fiap.report.usecase.dto.IdentifiedRisk;
import com.fiap.report.usecase.dto.GeneratedRecommendation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class TestController {

    private final CreateReportUseCase createReportUseCase;

    @GetMapping("/safe")
    public ResponseEntity<String> safeEndpoint(@RequestParam String id) {
        log.info("Safe endpoint called with id: {}", id);
        
        try {
            UUID uuid = UUID.fromString(id);
            return ResponseEntity.ok("Safe response for UUID: " + uuid.toString());
        } catch (Exception e) {
            log.error("Error in safe endpoint: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/simulate-ai-response")
    public ResponseEntity<String> simulateAIResponse() {
        log.info("Simulating AI analysis response...");
        
        // Criar dados de teste simulando retorno da IA
        AIAnalysisResult aiResult = AIAnalysisResult.builder()
                .diagramId(UUID.randomUUID())
                .userId("test-user-123")
                .extractedComponents(createTestComponents())
                .identifiedRisks(createTestRisks())
                .generatedRecommendations(createTestRecommendations())
                .modelVersion("gpt-4-vision-preview")
                .confidenceScore(0.92)
                .processingTimeMs(2500L)
                .build();

        try {
            // Executar o use case como se viesse do listener
            var report = createReportUseCase.execute(aiResult.getDiagramId(), aiResult);
            
            log.info("Test report created successfully: {}", report.getId());
            return ResponseEntity.ok("Test report created with ID: " + report.getId());
            
        } catch (Exception e) {
            log.error("Error creating test report", e);
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    private List<ExtractedComponent> createTestComponents() {
        return List.of(
            ExtractedComponent.builder()
                    .name("API Gateway")
                    .type("API_GATEWAY")
                    .connections(List.of("user-service", "order-service"))
                    .properties(java.util.Map.of(
                            "protocol", "REST",
                            "rateLimit", "1000 req/s"
                    ))
                    .technology("Spring Cloud Gateway")
                    .description("Gateway for routing requests")
                    .build(),
            ExtractedComponent.builder()
                    .name("User Service")
                    .type("MICROSERVICE")
                    .connections(List.of("database"))
                    .properties(java.util.Map.of(
                            "port", "8081",
                            "framework", "Spring Boot"
                    ))
                    .technology("Java Spring Boot")
                    .description("User management service")
                    .build()
        );
    }

    private List<IdentifiedRisk> createTestRisks() {
        return List.of(
            IdentifiedRisk.builder()
                    .id("risk-1")
                    .description("Single point of failure in database")
                    .level("HIGH")
                    .affectedComponent("database")
                    .category("AVAILABILITY")
                    .mitigation(List.of(
                            "Implement database clustering",
                            "Add read replicas",
                            "Setup automatic failover"
                    ))
                    .severityScore(8)
                    .impact("Service downtime if database fails")
                    .build(),
            IdentifiedRisk.builder()
                    .id("risk-2")
                    .description("No authentication in API Gateway")
                    .level("CRITICAL")
                    .affectedComponent("API Gateway")
                    .category("SECURITY")
                    .mitigation(List.of(
                            "Implement OAuth 2.0",
                            "Add JWT validation",
                            "Setup rate limiting"
                    ))
                    .severityScore(9)
                    .impact("Unauthorized access to services")
                    .build()
        );
    }

    private List<GeneratedRecommendation> createTestRecommendations() {
        return List.of(
            GeneratedRecommendation.builder()
                    .id("rec-1")
                    .description("Implement circuit breaker pattern")
                    .targetComponent("API Gateway")
                    .type("RESILIENCE")
                    .priority("HIGH")
                    .rationale("Prevents cascade failures between services")
                    .effort("MEDIUM")
                    .steps(List.of(
                            "Add Hystrix or Resilience4j",
                            "Configure fallback methods",
                            "Setup monitoring and alerts"
                    ))
                    .build(),
            GeneratedRecommendation.builder()
                    .id("rec-2")
                    .description("Add distributed tracing")
                    .targetComponent("All Services")
                    .type("MONITORING")
                    .priority("MEDIUM")
                    .rationale("Improve observability and debugging")
                    .effort("LOW")
                    .steps(List.of(
                            "Integrate Zipkin or Jaeger",
                            "Add tracing IDs to requests",
                            "Setup centralized logging"
                    ))
                    .build()
        );
    }
}
