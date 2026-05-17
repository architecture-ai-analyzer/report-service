package com.fiap.report.infrastructure.converter;

import com.fiap.report.domain.risk.RiskCategory;
import com.fiap.report.domain.risk.RiskData;
import com.fiap.report.domain.risk.RiskLevel;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class RiskDataListConverterTest {

    private final RiskDataListConverter converter = new RiskDataListConverter();

    @Test
    void convert_nullOrEmpty_returnsEmptyJsonAndEmptyList() {
        assertThat(converter.convertToDatabaseColumn(null)).isEqualTo("[]");
        assertThat(converter.convertToDatabaseColumn(List.of())).isEqualTo("[]");

        assertThat(converter.convertToEntityAttribute(null)).isEmpty();
        assertThat(converter.convertToEntityAttribute("")) .isEmpty();
    }

    @Test
    void convert_roundTrip_withData() {
        RiskData r = RiskData.builder()
                .id("rk1")
                .description("Failover missing")
                .level(RiskLevel.HIGH)
                .category(RiskCategory.RELIABILITY)
                .build();

        String json = converter.convertToDatabaseColumn(List.of(r));
        assertThat(json).contains("Failover missing");

        var list = converter.convertToEntityAttribute(json);
        assertThat(list).hasSize(1);
        assertThat(list.get(0).getDescription()).isEqualTo("Failover missing");
    }

    @Test
    void convertToEntityAttribute_invalidJson_returnsEmptyList() {
        var list = converter.convertToEntityAttribute("invalid json");
        assertThat(list).isEmpty();
    }
}
