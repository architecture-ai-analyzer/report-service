package com.fiap.report.infrastructure.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportSummaryResponse {
    private UUID id;
    private UUID diagramId;
    private String userId;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public static ReportSummaryResponse from(com.fiap.report.domain.report.AnalysisReport report) {
        return ReportSummaryResponse.builder()
                .id(report.getId())
                .diagramId(report.getDiagramId())
                .userId(report.getUserId())
                .status(report.getStatus().name())
                .createdAt(report.getGeneratedAt())
                .updatedAt(report.getGeneratedAt())
                .build();
    }
}
