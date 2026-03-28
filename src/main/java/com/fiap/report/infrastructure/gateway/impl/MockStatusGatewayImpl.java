package com.fiap.report.infrastructure.gateway.impl;

import com.fiap.report.gateway.StatusGateway;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
public class MockStatusGatewayImpl implements StatusGateway {

    @Override
    public void updateStatus(UUID diagramId, String status) {
        log.info("Mock: Updating status for diagram {} to {}", diagramId, status);
        // Implementação mock simples
    }
}
