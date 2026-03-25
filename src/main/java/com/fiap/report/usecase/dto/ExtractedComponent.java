package com.fiap.report.usecase.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExtractedComponent {
    private String name;
    private String type;
    private java.util.List<String> connections;
    private Map<String, Object> properties;
    private String technology;
    private String description;
}
