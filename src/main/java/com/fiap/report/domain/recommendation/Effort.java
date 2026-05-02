package com.fiap.report.domain.recommendation;

public enum Effort {
    LOW("Low", "Hours to days"),
    MEDIUM("Medium", "Days to weeks"),
    HIGH("High", "Weeks to months"),
    VERY_HIGH("Very High", "Months to quarters");

    private final String displayName;
    private final String description;

    Effort(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }
}
