package com.fiap.report.infrastructure.gateway.impl;

import com.fiap.report.gateway.ReportMetricsGateway;
import com.fiap.report.gateway.StatusGateway;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ObservedStatusGateway implements StatusGateway {

    private final SQSStatusGatewayImpl delegate;
    private final ReportMetricsGateway reportMetricsGateway;

    public ObservedStatusGateway(SQSStatusGatewayImpl delegate, ReportMetricsGateway reportMetricsGateway) {
        this.delegate = delegate;
        this.reportMetricsGateway = reportMetricsGateway;
    }

    @Override
    public void updateStatus(UUID diagramId, String status) {
        long startMillis = System.currentTimeMillis();
        delegate.updateStatus(diagramId, status);
        long durationSeconds = (System.currentTimeMillis() - startMillis) / 1000;
        reportMetricsGateway.recordStatusTransitionDuration(durationSeconds, "status:" + status);
    }
}
