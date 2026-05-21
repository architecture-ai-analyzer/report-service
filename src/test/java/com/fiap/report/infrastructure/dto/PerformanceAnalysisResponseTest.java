package com.fiap.report.infrastructure.dto;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PerformanceAnalysisResponseTest {

    @Test
    void builder_createsValidResponse() {
        PerformanceAnalysisResponse response = PerformanceAnalysisResponse.builder()
                .metrics(List.of())
                .bottlenecks(List.of("bottleneck1"))
                .capacity(null)
                .build();

        assertThat(response).isNotNull();
        assertThat(response.getBottlenecks()).containsExactly("bottleneck1");
    }

    @Test
    void from_createsResponseFromComponents() {
        List<ComponentResponse> components = List.of(
                ComponentResponse.builder().id("1").name("Service").type("MICROSERVICE").build()
        );

        PerformanceAnalysisResponse response = PerformanceAnalysisResponse.from(components);

        assertThat(response).isNotNull();
        assertThat(response.getMetrics()).isNotEmpty();
        assertThat(response.getCapacity()).isNotNull();
    }
}
