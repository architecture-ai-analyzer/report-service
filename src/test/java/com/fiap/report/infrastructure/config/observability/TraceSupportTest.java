package com.fiap.report.infrastructure.config.observability;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;

import static org.assertj.core.api.Assertions.assertThat;

class TraceSupportTest {

    @AfterEach
    void tearDown() {
        TraceSupport.clearErrorMdc();
        MDC.clear();
    }

    @Test
    void putErrorMdc_shouldPopulateMdcFields() {
        Exception ex = new IllegalArgumentException("invalid payload");

        TraceSupport.putErrorMdc(ex, "BAD_REQUEST", 400);

        assertThat(MDC.get("error")).isEqualTo("true");
        assertThat(MDC.get("error.type")).isEqualTo("IllegalArgumentException");
        assertThat(MDC.get("error.code")).isEqualTo("BAD_REQUEST");
        assertThat(MDC.get("http.status_code")).isEqualTo("400");
    }

    @Test
    void clearErrorMdc_shouldRemoveErrorFields() {
        TraceSupport.putErrorMdc(new RuntimeException("x"), "ERR", 500);
        TraceSupport.clearErrorMdc();

        assertThat(MDC.get("error")).isNull();
        assertThat(MDC.get("error.code")).isNull();
    }

    @Test
    void tagActiveSpan_shouldNotThrowWithoutActiveSpan() {
        TraceSupport.tagActiveSpan("operation.type", "generateReport");
    }
}
