package com.fiap.report.infrastructure.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProcessingStatusResponse {
    private UUID id;
    private String status;
    private Integer progress;
    private String estimatedTimeRemaining;
    private String currentStep;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
