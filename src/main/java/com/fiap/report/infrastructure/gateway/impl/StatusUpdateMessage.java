package com.fiap.report.infrastructure.gateway.impl;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatusUpdateMessage {
    private UUID diagramId;
    private String status;
    private String timestamp;
}
