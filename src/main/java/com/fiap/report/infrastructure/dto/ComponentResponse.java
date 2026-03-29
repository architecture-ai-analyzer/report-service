package com.fiap.report.infrastructure.dto;

import com.fiap.report.domain.component.ComponentData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComponentResponse {
    private String id;
    private String name;
    private String type;
    private List<String> connections;
    private int connectionsCount;
    private java.util.Map<String, Object> properties;
    private String technology;
    private String description;
    private String criticality;

    public static ComponentResponse from(ComponentData component) {
        String criticality = switch(component.getType().name()) {
            case "DATABASE" -> "Alta";
            case "API" -> "Alta";
            case "MICROSERVICE" -> "Média";
            default -> "Baixa";
        };
        
        List<String> connections = component.getConnections();
        int connectionsCount = connections != null ? connections.size() : 0;
        
        return ComponentResponse.builder()
                .id(component.getId())
                .name(component.getName())
                .type(component.getType().name())
                .connections(connections)
                .connectionsCount(connectionsCount)
                .properties(component.getProperties())
                .technology(component.getTechnology())
                .description(component.getDescription())
                .criticality(criticality)
                .build();
    }
}
