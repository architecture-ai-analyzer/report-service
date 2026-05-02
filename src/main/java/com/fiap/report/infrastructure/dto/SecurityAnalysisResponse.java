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
public class SecurityAnalysisResponse {
    private String overallRisk;
    private int risksFound;
    private List<SecurityRiskResponse> risks;
    private ComplianceResponse compliance;
    
    public static SecurityAnalysisResponse from(List<RiskResponse> risks) {
        List<RiskResponse> safeRisks = risks != null ? risks : List.of();
        
        int criticalRisks = (int) safeRisks.stream()
                .filter(risk -> "CRITICAL".equals(risk.getLevel()))
                .count();
        
        int highRisks = (int) safeRisks.stream()
                .filter(risk -> "HIGH".equals(risk.getLevel()))
                .count();
        
        String overallRisk = criticalRisks > 0 ? "Crítico" : 
                            highRisks > 0 ? "Alto" : "Médio";
        
        List<SecurityRiskResponse> securityRisks = safeRisks.stream()
                .map(SecurityRiskResponse::from)
                .toList();
        
        ComplianceResponse compliance = ComplianceResponse.builder()
                .lgpd(true)
                .iso27001(false)
                .owasp(true)
                .build();
        
        return SecurityAnalysisResponse.builder()
                .overallRisk(overallRisk)
                .risksFound(safeRisks.size())
                .risks(securityRisks)
                .compliance(compliance)
                .build();
    }
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class SecurityRiskResponse {
    private String title;
    private String description;
    private String level;
    private String recommendation;
    
    public static SecurityRiskResponse from(RiskResponse risk) {
        String level = switch(risk.getLevel()) {
            case "CRITICAL" -> "Crítico";
            case "HIGH" -> "Alto";
            case "MEDIUM" -> "Médio";
            case "LOW" -> "Baixo";
            default -> "Médio";
        };
        
        return SecurityRiskResponse.builder()
                .title(risk.getDescription())
                .description(risk.getImpact())
                .level(level)
                .recommendation(risk.getMitigation() != null && !risk.getMitigation().isEmpty() 
                        ? risk.getMitigation().get(0) 
                        : "Implementar controles de segurança")
                .build();
    }
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class ComplianceResponse {
    private boolean lgpd;
    private boolean iso27001;
    private boolean owasp;
}
