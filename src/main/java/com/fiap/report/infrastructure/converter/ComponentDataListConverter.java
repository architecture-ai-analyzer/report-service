package com.fiap.report.infrastructure.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fiap.report.domain.component.ComponentData;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@Converter
public class ComponentDataListConverter implements AttributeConverter<List<ComponentData>, String> {

    private static final ObjectMapper objectMapper;

    static {
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Override
    public String convertToDatabaseColumn(List<ComponentData> attribute) {
        log.info("🔄 Converting components to JSON: {} items", attribute != null ? attribute.size() : 0);
        if (attribute == null || attribute.isEmpty()) {
            return null;
        }
        try {
            String json = objectMapper.writeValueAsString(attribute);
            log.info("✅ Converted to JSON: {}", json);
            return json;
        } catch (JsonProcessingException e) {
            log.error("Error converting components to JSON", e);
            return null;
        }
    }

    @Override
    public List<ComponentData> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.trim().isEmpty()) {
            return List.of();
        }
        try {
            return objectMapper.readValue(dbData, new TypeReference<List<ComponentData>>() {});
        } catch (JsonProcessingException e) {
            log.error("Error converting JSON to components", e);
            return List.of();
        }
    }
}
