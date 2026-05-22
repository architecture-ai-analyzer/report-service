package com.fiap.report.domain.component;

public enum ComponentType {
    DATABASE("Database"),
    API("API"),
    QUEUE("Queue"),
    CACHE("Cache"),
    LOAD_BALANCER("Load Balancer"),
    MICROSERVICE("Microservice"),
    FRONTEND("Frontend"),
    EXTERNAL("External Service"),
    MESSAGE_BROKER("Message Broker"),
    STORAGE("Storage"),
    SERVICE("Service");

    private final String displayName;

    ComponentType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
