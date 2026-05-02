package com.fiap.report.infrastructure.dto;

import com.fiap.report.domain.risk.RiskData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RiskResponse {
    private String id;
    private String description;
    private String level;
    private String affectedComponent;
    private String category;
    private List<String> mitigation;
    private Integer severityScore;
    private String impact;

    public static RiskResponse from(RiskData risk) {
        return RiskResponse.builder()
                .id(risk.getId())
                .description(risk.getDescription())
                .level(risk.getLevel().name())
                .affectedComponent(risk.getAffectedComponent())
                .category(risk.getCategory().name())
                .mitigation(risk.getMitigation())
                .severityScore(risk.getSeverityScore())
                .impact(risk.getImpact())
                .build();
    }
}
