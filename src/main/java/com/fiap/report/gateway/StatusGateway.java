package com.fiap.report.gateway;

import java.util.UUID;

public interface StatusGateway {
    void updateStatus(UUID diagramId, String status);
}
