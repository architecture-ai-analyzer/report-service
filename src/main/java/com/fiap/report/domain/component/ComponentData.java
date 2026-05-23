package com.fiap.report.domain.component;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComponentData {
    private String id;
    private String name;
    private ComponentType type;
    private List<String> connections;
    private Map<String, Object> properties;
    private String technology;
    private String description;
}
