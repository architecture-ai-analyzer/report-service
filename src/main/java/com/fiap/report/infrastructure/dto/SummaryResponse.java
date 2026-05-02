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
public class SummaryResponse {
    private int totalComponents;
    private int securityScore;
    private int performanceScore;
    private int architectureScore;
    
    public static SummaryResponse from(int totalComponents, List<RiskResponse> risks) {
        int criticalRisks = (int) risks.stream()
                .filter(risk -> "CRITICAL".equals(risk.getLevel()))
                .count();
        
        int highRisks = (int) risks.stream()
                .filter(risk -> "HIGH".equals(risk.getLevel()))
                .count();
        
        int securityScore = Math.max(0, 100 - (criticalRisks * 20) - (highRisks * 10));
        int performanceScore = Math.max(20, 100 - (totalComponents * 2));
        int architectureScore = Math.max(30, 90 - (totalComponents * 3));
        
        return SummaryResponse.builder()
                .totalComponents(totalComponents)
                .securityScore(securityScore)
                .performanceScore(performanceScore)
                .architectureScore(architectureScore)
                .build();
    }
    
    public static SummaryResponse defaultSummary() {
        return SummaryResponse.builder()
                .totalComponents(0)
                .securityScore(100)
                .performanceScore(100)
                .architectureScore(100)
                .build();
    }
}
