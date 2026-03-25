package com.fiap.report.usecase.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IdentifiedRisk {
    private String id;
    private String description;
    private String level;
    private String affectedComponent;
    private String category;
    private List<String> mitigation;
    private Integer severityScore;
    private String impact;
}
