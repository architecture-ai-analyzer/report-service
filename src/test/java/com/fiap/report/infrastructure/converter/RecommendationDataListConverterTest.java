package com.fiap.report.infrastructure.converter;

import com.fiap.report.domain.recommendation.Effort;
import com.fiap.report.domain.recommendation.Priority;
import com.fiap.report.domain.recommendation.RecommendationData;
import com.fiap.report.domain.recommendation.RecommendationType;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class RecommendationDataListConverterTest {

    private final RecommendationDataListConverter converter = new RecommendationDataListConverter();

    @Test
    void convert_nullOrEmpty_returnsEmptyJsonAndEmptyList() {
        assertThat(converter.convertToDatabaseColumn(null)).isEqualTo("[]");
        assertThat(converter.convertToDatabaseColumn(List.of())).isEqualTo("[]");

        assertThat(converter.convertToEntityAttribute(null)).isEmpty();
        assertThat(converter.convertToEntityAttribute("")) .isEmpty();
    }

    @Test
    void convert_roundTrip_withData() {
        RecommendationData r = RecommendationData.builder()
                .id("r1")
                .description("Use cache")
                .priority(Priority.MEDIUM)
                .type(RecommendationType.PERFORMANCE)
                .effort(Effort.LOW)
                .build();

        String json = converter.convertToDatabaseColumn(List.of(r));
        assertThat(json).contains("Use cache");

        var list = converter.convertToEntityAttribute(json);
        assertThat(list).hasSize(1);
        assertThat(list.get(0).getDescription()).isEqualTo("Use cache");
    }

    @Test
    void convertToEntityAttribute_invalidJson_returnsEmptyList() {
        var list = converter.convertToEntityAttribute("invalid json");
        assertThat(list).isEmpty();
    }
}
