package com.fiap.report.domain.risk;

public enum RiskCategory {
    SECURITY("Security"),
    PERFORMANCE("Performance"),
    SCALABILITY("Scalability"),
    RELIABILITY("Reliability"),
    MAINTAINABILITY("Maintainability"),
    COST("Cost"),
    OPERATIONAL("Operational"),
    DATA("Data"),
    OTHER("Other"),
    LOAD_BALANCER("Load Balancer"),
    COMPLIANCE("Compliance");

    private final String displayName;

    RiskCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
