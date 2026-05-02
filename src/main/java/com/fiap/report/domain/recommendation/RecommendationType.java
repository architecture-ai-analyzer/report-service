package com.fiap.report.domain.recommendation;

public enum RecommendationType {
    SECURITY("Security"),
    PERFORMANCE("Performance"),
    SCALABILITY("Scalability"),
    RELIABILITY("Reliability"),
    MAINTAINABILITY("Maintainability"),
    COST_OPTIMIZATION("Cost Optimization"),
    MONITORING("Monitoring"),
    DOCUMENTATION("Documentation");

    private final String displayName;

    RecommendationType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
