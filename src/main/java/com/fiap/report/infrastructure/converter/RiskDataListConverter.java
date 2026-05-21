package com.fiap.report.infrastructure.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fiap.report.domain.risk.RiskData;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@Converter
public class RiskDataListConverter implements AttributeConverter<List<RiskData>, String> {

    private final ObjectMapper objectMapper;

    public RiskDataListConverter() {
        this(createDefaultObjectMapper());
    }

    RiskDataListConverter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    private static ObjectMapper createDefaultObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }

    @Override
    public String convertToDatabaseColumn(List<RiskData> attribute) {
        if (attribute == null || attribute.isEmpty()) {
            return "[]";
        }
        try {
            return objectMapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            return "[]";
        }
    }

    @Override
    public List<RiskData> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.trim().isEmpty()) {
            return List.of();
        }
        try {
            return objectMapper.readValue(dbData, new TypeReference<List<RiskData>>() {});
        } catch (JsonProcessingException e) {
            return List.of();
        }
    }
}
