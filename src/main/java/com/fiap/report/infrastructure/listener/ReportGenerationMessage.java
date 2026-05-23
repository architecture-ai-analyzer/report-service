package com.fiap.report.infrastructure.listener;

import com.fiap.report.usecase.dto.AIAnalysisResult;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportGenerationMessage {
    private UUID diagramId;
    private String userId;
    private AIAnalysisResult aiResult;
    private String timestamp;
}
