package com.fiap.report.infrastructure.gateway.impl;

import com.fiap.report.gateway.ReportMetricsGateway;
import com.timgroup.statsd.StatsDClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ReportMetricsGatewayImpl implements ReportMetricsGateway {

    private static final Logger logger = LoggerFactory.getLogger(ReportMetricsGatewayImpl.class);
    private final StatsDClient statsDClient;

    public ReportMetricsGatewayImpl(StatsDClient statsDClient) {
        this.statsDClient = statsDClient;
    }

    @Override
    public void recordReportCreated(String statusTag) {
        try {
            statsDClient.incrementCounter("report.created", statusTag);
        } catch (Exception e) {
            logger.warn("Failed to send Datadog metric report.created: {}", e.getMessage());
        }
    }

    @Override
    public void recordStatusTransitionDuration(long durationInSeconds, String statusTag) {
        try {
            statsDClient.gauge("report.status.transition.duration", durationInSeconds, statusTag);
        } catch (Exception e) {
            logger.warn("Failed to send Datadog metric report.status.transition.duration: {}", e.getMessage());
        }
    }

    @Override
    public void recordPipelineDuration(long durationInSeconds, String statusTag) {
        try {
            statsDClient.gauge("report.pipeline.duration", durationInSeconds, statusTag);
        } catch (Exception e) {
            logger.warn("Failed to send Datadog metric report.pipeline.duration: {}", e.getMessage());
        }
    }

    @Override
    public void recordPdfGenerated(String statusTag) {
        try {
            statsDClient.incrementCounter("report.pdf.generated", statusTag);
        } catch (Exception e) {
            logger.warn("Failed to send Datadog metric report.pdf.generated: {}", e.getMessage());
        }
    }
}
