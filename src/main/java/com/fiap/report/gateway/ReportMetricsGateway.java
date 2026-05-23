package com.fiap.report.gateway;

public interface ReportMetricsGateway {

    void recordReportCreated(String statusTag);

    void recordStatusTransitionDuration(long durationInSeconds, String statusTag);

    void recordPipelineDuration(long durationInSeconds, String statusTag);

    void recordPdfGenerated(String statusTag);
}
