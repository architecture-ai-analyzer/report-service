package com.fiap.report.infrastructure.gateway.impl;

import com.timgroup.statsd.StatsDClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ReportMetricsGatewayImplTest {

    @Mock
    private StatsDClient statsDClient;

    private ReportMetricsGatewayImpl metricsGateway;

    @BeforeEach
    void setUp() {
        metricsGateway = new ReportMetricsGatewayImpl(statsDClient);
    }

    @Test
    void recordReportCreated_shouldIncrementCounter() {
        metricsGateway.recordReportCreated("status:ANALISADO");

        verify(statsDClient).incrementCounter("report.created", "status:ANALISADO");
    }

    @Test
    void recordStatusTransitionDuration_shouldSendGauge() {
        metricsGateway.recordStatusTransitionDuration(120L, "status:EM_PROCESSAMENTO");

        verify(statsDClient).gauge("report.status.transition.duration", 120L, "status:EM_PROCESSAMENTO");
    }

    @Test
    void recordPipelineDuration_shouldSendGauge() {
        metricsGateway.recordPipelineDuration(300L, "status:ERRO");

        verify(statsDClient).gauge("report.pipeline.duration", 300L, "status:ERRO");
    }

    @Test
    void recordPdfGenerated_shouldIncrementCounter() {
        metricsGateway.recordPdfGenerated("status:success");

        verify(statsDClient).incrementCounter("report.pdf.generated", "status:success");
    }

    @Test
    void recordReportCreated_shouldNotThrowWhenStatsDFails() {
        doThrow(new RuntimeException("statsd unavailable"))
                .when(statsDClient)
                .incrementCounter("report.created", "status:ERRO");

        metricsGateway.recordReportCreated("status:ERRO");
    }
}
