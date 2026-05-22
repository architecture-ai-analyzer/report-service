package com.fiap.report.infrastructure.gateway.impl;

import com.fiap.report.gateway.ReportMetricsGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ObservedStatusGatewayTest {

    @Mock
    private SQSStatusGatewayImpl delegate;

    @Mock
    private ReportMetricsGateway reportMetricsGateway;

    private ObservedStatusGateway observedStatusGateway;

    @BeforeEach
    void setUp() {
        observedStatusGateway = new ObservedStatusGateway(delegate, reportMetricsGateway);
    }

    @Test
    void updateStatus_shouldDelegateAndRecordMetric() {
        UUID diagramId = UUID.randomUUID();

        observedStatusGateway.updateStatus(diagramId, "ANALISADO");

        verify(delegate).updateStatus(diagramId, "ANALISADO");
        verify(reportMetricsGateway).recordStatusTransitionDuration(anyLong(), eq("status:ANALISADO"));
    }
}
