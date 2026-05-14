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
        log.info("Endpoint seguro chamado com id: {}", id);

        try {
            UUID uuid = UUID.fromString(id);
            return ResponseEntity.ok("Resposta segura para UUID: " + uuid.toString());
        } catch (Exception e) {
            log.error("Erro no endpoint seguro: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body("Erro: " + e.getMessage());
        }
    }

    @PostMapping("/simulate-ai-response")
    public ResponseEntity<String> simulateAIResponse() {
        log.info("Simulando resposta da análise de IA...");

        AIAnalysisResult aiResult = AIAnalysisResult.builder()
                .diagramId(UUID.randomUUID())
                .userId("test-user-123")
                .templateId("template-teste-123")
                .extractedComponents(createTestComponents())
                .identifiedRisks(createTestRisks())
                .generatedRecommendations(createTestRecommendations())
                .modelVersion("gpt-4-vision-preview")
                .confidenceScore(0.92)
                .processingTimeMs(2500L)
                .build();

        try {
            var report = createReportUseCase.execute(aiResult.getDiagramId(), aiResult);

            log.info("Relatório de teste criado com sucesso: {}", report.getId());
            return ResponseEntity.ok("Relatório de teste criado com ID: " + report.getId());

        } catch (Exception e) {
            log.error("Erro ao criar relatório de teste", e);
            return ResponseEntity.badRequest().body("Erro: " + e.getMessage());
        }
    }

    private List<ExtractedComponent> createTestComponents() {
        return List.of(
                ExtractedComponent.builder()
                        .name("API Gateway")
                        .type("API_GATEWAY")
                        .connections(List.of("user-service", "order-service"))
                        .properties(java.util.Map.of("protocol", "REST", "rateLimit", "1000 req/s"))
                        .technology("Spring Cloud Gateway")
                        .description("Gateway para roteamento de requisições")
                        .build(),
                ExtractedComponent.builder()
                        .name("User Service")
                        .type("MICROSERVICE")
                        .connections(List.of("database"))
                        .properties(java.util.Map.of("port", "8081", "framework", "Spring Boot"))
                        .technology("Java Spring Boot")
                        .description("Serviço de gerenciamento de usuários")
                        .build()
        );
    }

    private List<IdentifiedRisk> createTestRisks() {
        return List.of(
                IdentifiedRisk.builder()
                        .id("risk-1")
                        .description("Ponto único de falha no banco de dados")
                        .level("HIGH")
                        .affectedComponent("database")
                        .category("AVAILABILITY")
                        .mitigation(List.of("Implementar clustering no banco de dados", "Adicionar réplicas de leitura", "Configurar failover automático"))
                        .severityScore(8)
                        .impact("Indisponibilidade do serviço se o banco falhar")
                        .build(),
                IdentifiedRisk.builder()
                        .id("risk-2")
                        .description("Sem autenticação no API Gateway")
                        .level("CRITICAL")
                        .affectedComponent("API Gateway")
                        .category("SECURITY")
                        .mitigation(List.of("Implementar OAuth 2.0", "Adicionar validação JWT", "Configurar rate limiting"))
                        .severityScore(9)
                        .impact("Acesso não autorizado aos serviços")
                        .build()
        );
    }

    private List<GeneratedRecommendation> createTestRecommendations() {
        return List.of(
                GeneratedRecommendation.builder()
                        .id("rec-1")
                        .description("Implementar padrão circuit breaker")
                        .targetComponent("API Gateway")
                        .type("RESILIENCE")
                        .priority("HIGH")
                        .rationale("Previne falhas em cascata entre serviços")
                        .effort("MEDIUM")
                        .steps(List.of("Adicionar Hystrix ou Resilience4j", "Configurar métodos de fallback", "Configurar monitoramento e alertas"))
                        .build(),
                GeneratedRecommendation.builder()
                        .id("rec-2")
                        .description("Adicionar tracing distribuído")
                        .targetComponent("All Services")
                        .type("MONITORING")
                        .priority("MEDIUM")
                        .rationale("Melhora observabilidade e debugging")
                        .effort("LOW")
                        .steps(List.of("Integrar Zipkin ou Jaeger", "Adicionar IDs de tracing nas requisições", "Configurar logging centralizado"))
                        .build()
        );
    }
}