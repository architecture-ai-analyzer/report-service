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
    private java.util.Map<String, Object> properties;
    private String technology;
    private String description;

    public static ComponentResponse from(ComponentData component) {
        return ComponentResponse.builder()
                .id(component.getId())
                .name(component.getName())
                .type(component.getType().name())
                .connections(component.getConnections())
                .properties(component.getProperties())
                .technology(component.getTechnology())
                .description(component.getDescription())
                .build();
    }
}
