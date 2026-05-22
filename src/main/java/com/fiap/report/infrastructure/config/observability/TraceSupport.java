package com.fiap.report.infrastructure.config.observability;

import io.opentracing.Span;
import io.opentracing.util.GlobalTracer;
import org.slf4j.MDC;

public final class TraceSupport {

    private static final String ERROR = "error";
    private static final String ERROR_TYPE = "error.type";
    private static final String ERROR_MESSAGE = "error.message";
    private static final String ERROR_CODE = "error.code";
    private static final String ERROR_STACK = "error.stack";
    private static final String UNKNOWN_ERROR = "Unknown error";

    private TraceSupport() {
    }

    public static void tagActiveSpan(String key, String value) {
        if (value == null) {
            return;
        }
        Span span = GlobalTracer.get().activeSpan();
        if (span != null) {
            span.setTag(key, value);
        }
    }

    public static void addErrorToSpan(Exception ex, String errorCode) {
        Span span = GlobalTracer.get().activeSpan();
        if (span != null) {
            span.setTag(ERROR, true);
            span.setTag(ERROR_TYPE, ex.getClass().getName());
            span.setTag(ERROR_MESSAGE, ex.getMessage() != null ? ex.getMessage() : UNKNOWN_ERROR);
            if (errorCode != null) {
                span.setTag(ERROR_CODE, errorCode);
            }
            span.setTag(ERROR_STACK, getStackTrace(ex));
        }
    }

    public static void putErrorMdc(Exception ex, String errorCode, int httpStatus) {
        MDC.put(ERROR, "true");
        MDC.put(ERROR_TYPE, ex.getClass().getSimpleName());
        if (errorCode != null) {
            MDC.put(ERROR_CODE, errorCode);
        }
        MDC.put(ERROR_MESSAGE, ex.getMessage() != null ? ex.getMessage() : UNKNOWN_ERROR);
        MDC.put("http.status_code", String.valueOf(httpStatus));
    }

    public static void clearErrorMdc() {
        MDC.remove(ERROR);
        MDC.remove(ERROR_TYPE);
        MDC.remove(ERROR_CODE);
        MDC.remove(ERROR_MESSAGE);
        MDC.remove("http.status_code");
    }

    private static String getStackTrace(Exception ex) {
        var sw = new java.io.StringWriter();
        var pw = new java.io.PrintWriter(sw);
        ex.printStackTrace(pw);
        return sw.toString();
    }
}
