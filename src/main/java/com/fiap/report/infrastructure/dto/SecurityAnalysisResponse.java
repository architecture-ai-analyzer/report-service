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

    private static final String MEDIO = "Médio";

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
        
        String overallRisk = calculateOverallRisk(criticalRisks, highRisks);
        
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

    private static String calculateOverallRisk(int criticalRisks, int highRisks) {
        if (criticalRisks > 0) {
            return "Crítico";
        } else if (highRisks > 0) {
            return "Alto";
        } else {
            return MEDIO;
        }
    }
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class SecurityRiskResponse {

    private static final String MEDIO = "Médio";

    private String title;
    private String description;
    private String level;
    private String recommendation;
    
    public static SecurityRiskResponse from(RiskResponse risk) {
        String level = switch(risk.getLevel()) {
            case "CRITICAL" -> "Crítico";
            case "HIGH" -> "Alto";
            case "MEDIUM" -> MEDIO;
            case "LOW" -> "Baixo";
            default -> MEDIO;
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
