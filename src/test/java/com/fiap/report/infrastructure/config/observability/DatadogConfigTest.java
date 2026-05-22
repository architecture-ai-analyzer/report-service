package com.fiap.report.infrastructure.config.observability;

import com.timgroup.statsd.StatsDClient;
import io.opentracing.Tracer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = DatadogConfigTest.TestConfig.class)
@TestPropertySource(properties = {
        "dd.trace.enabled=false",
        "datadog.statsd.host=localhost",
        "datadog.statsd.port=8125"
})
class DatadogConfigTest {

    @Autowired(required = false)
    private Tracer tracer;

    @Autowired(required = false)
    private StatsDClient statsDClient;

    @Test
    void contextLoads_datadogBeansAreRegistered() {
        assertThat(tracer).isNotNull();
        assertThat(statsDClient).isNotNull();
    }

    @Configuration
    @Import(DatadogConfig.class)
    static class TestConfig {
    }
}
