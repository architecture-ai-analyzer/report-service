package com.fiap.report.domain.risk;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RiskData {
    private String id;
    private String description;
    private RiskLevel level;
    private String affectedComponent;
    private RiskCategory category;
    private List<String> mitigation;
    private Integer severityScore;
    private String impact;
}
