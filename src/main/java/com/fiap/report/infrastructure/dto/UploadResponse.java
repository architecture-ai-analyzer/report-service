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
public class UploadResponse {
    private UUID uploadId;
    private String fileName;
    private Long fileSize;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
