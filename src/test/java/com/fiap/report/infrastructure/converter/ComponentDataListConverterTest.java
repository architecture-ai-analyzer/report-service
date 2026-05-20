package com.fiap.report.infrastructure.converter;

import com.fiap.report.domain.component.ComponentData;
import com.fiap.report.domain.component.ComponentType;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ComponentDataListConverterTest {

    private final ComponentDataListConverter converter = new ComponentDataListConverter();

    @Test
    void convert_nullOrEmpty_returnsEmptyJsonAndEmptyList() {
        assertThat(converter.convertToDatabaseColumn(null)).isEqualTo("[]");
        assertThat(converter.convertToDatabaseColumn(List.of())).isEqualTo("[]");

        assertThat(converter.convertToEntityAttribute(null)).isEmpty();
        assertThat(converter.convertToEntityAttribute("   ")).isEmpty();
    }

    @Test
    void convert_roundTrip_withData() {
        ComponentData c = ComponentData.builder()
                .id("id1")
                .name("API Gateway")
                .type(ComponentType.API)
                .technology("Spring")
                .description("desc")
                .connections(List.of("svc1"))
                .build();

        String json = converter.convertToDatabaseColumn(List.of(c));
        assertThat(json).contains("API Gateway");

        var list = converter.convertToEntityAttribute(json);
        assertThat(list).hasSize(1);
        assertThat(list.get(0).getName()).isEqualTo("API Gateway");
    }

    @Test
    void convertToEntityAttribute_invalidJson_returnsEmptyList() {
        var list = converter.convertToEntityAttribute("invalid json");
        assertThat(list).isEmpty();
    }
}
