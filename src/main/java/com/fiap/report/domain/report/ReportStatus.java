package com.fiap.report.domain.report;

public enum ReportStatus {
    GENERATED("Generated"),
    PROCESSING("Processing"),
    COMPLETED("Completed"),
    ERROR("Error");

    private final String displayName;

    ReportStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
