package com.fiap.report.infrastructure.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArchitectureAnalysisResponse {
    private List<PatternResponse> patterns;
    private String complexity;
    private String maintainability;
    private String scalability;
    
    public static ArchitectureAnalysisResponse from(List<ComponentResponse> components) {
        List<PatternResponse> patterns = List.of(
            PatternResponse.builder()
                .pattern("Microsserviços")
                .detected(true)
                .description("Arquitetura baseada em microsserviços")
                .benefits(List.of("Escalabilidade", "Resiliência", "Manutenibilidade"))
                .recommendation("Manter comunicação assíncrona")
                .build(),
            PatternResponse.builder()
                .pattern("API Gateway")
                .detected(true)
                .description("Gateway centralizado para APIs")
                .benefits(List.of("Segurança", "Roteamento", "Monitoramento"))
                .recommendation("Implementar rate limiting")
                .build()
        );
        
        String complexity = calculateComplexity(components);
        
        return ArchitectureAnalysisResponse.builder()
                .patterns(patterns)
                .complexity(complexity)
                .maintainability("Média")
                .scalability("Alta")
                .build();
    }

    private static String calculateComplexity(List<ComponentResponse> components) {
        if (components.size() > 5) {
            return "Alta";
        } else if (components.size() > 3) {
            return "Média";
        } else {
            return "Baixa";
        }
    }
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class PatternResponse {
    private String pattern;
    private boolean detected;
    private String description;
    private List<String> benefits;
    private String recommendation;
}
