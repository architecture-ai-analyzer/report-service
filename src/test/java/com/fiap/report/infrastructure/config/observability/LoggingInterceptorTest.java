package com.fiap.report.infrastructure.config.observability;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoggingInterceptorTest {

    private LoggingInterceptor loggingInterceptor;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @BeforeEach
    void setUp() {
        loggingInterceptor = new LoggingInterceptor();
    }

    @AfterEach
    void tearDown() {
        MDC.clear();
    }

    @Test
    void preHandle_shouldPopulateMdcFields() {
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/api/reports");
        when(request.getQueryString()).thenReturn("param=value");
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");

        boolean result = loggingInterceptor.preHandle(request, response, null);

        assertThat(result).isTrue();
        assertThat(MDC.get("request.id")).isNotNull();
        assertThat(MDC.get("http.method")).isEqualTo("GET");
        assertThat(MDC.get("http.path")).isEqualTo("/api/reports");
        assertThat(MDC.get("http.query_string")).isEqualTo("param=value");
        assertThat(MDC.get("http.remote_addr")).isEqualTo("127.0.0.1");
    }

    @Test
    void preHandle_shouldHandleNullQueryString() {
        when(request.getMethod()).thenReturn("POST");
        when(request.getRequestURI()).thenReturn("/api/reports");
        when(request.getQueryString()).thenReturn(null);
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");

        boolean result = loggingInterceptor.preHandle(request, response, null);

        assertThat(result).isTrue();
        assertThat(MDC.get("http.query_string")).isEqualTo("");
    }

    @Test
    void afterCompletion_shouldLogSuccess() {
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/api/reports");
        when(request.getAttribute("startTime")).thenReturn(System.currentTimeMillis() - 100L);
        when(response.getStatus()).thenReturn(200);

        loggingInterceptor.afterCompletion(request, response, null, null);

        // MDC is cleared at the end, so we just verify no exception is thrown
    }

    @Test
    void afterCompletion_shouldLogError() {
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/api/reports");
        when(request.getAttribute("startTime")).thenReturn(System.currentTimeMillis() - 100L);
        when(response.getStatus()).thenReturn(500);
        Exception ex = new RuntimeException("Test error");

        loggingInterceptor.afterCompletion(request, response, null, ex);

        // MDC is cleared at the end, so we just verify no exception is thrown
    }

    @Test
    void afterCompletion_shouldHandleNullStartTime() {
        when(request.getAttribute("startTime")).thenReturn(null);

        loggingInterceptor.afterCompletion(request, response, null, null);

        // MDC is cleared at the end, so we just verify no exception is thrown
    }

    @Test
    void afterCompletion_shouldHandleNullExceptionMessage() {
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/api/reports");
        when(request.getAttribute("startTime")).thenReturn(System.currentTimeMillis() - 100L);
        when(response.getStatus()).thenReturn(500);
        Exception ex = new RuntimeException((String) null);

        loggingInterceptor.afterCompletion(request, response, null, ex);

        // MDC is cleared at the end, so we just verify no exception is thrown
    }
}
