package com.fiap.report.infrastructure.dto;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ArchitectureAnalysisResponseTest {

    @Test
    void builder_createsValidResponse() {
        ArchitectureAnalysisResponse response = ArchitectureAnalysisResponse.builder()
                .patterns(List.of())
                .complexity("Alta")
                .maintainability("Média")
                .scalability("Alta")
                .build();

        assertThat(response).isNotNull();
        assertThat(response.getComplexity()).isEqualTo("Alta");
        assertThat(response.getMaintainability()).isEqualTo("Média");
        assertThat(response.getScalability()).isEqualTo("Alta");
    }

    @Test
    void from_createsResponseFromComponents() {
        List<ComponentResponse> components = List.of(
                ComponentResponse.builder().id("1").name("Service").type("MICROSERVICE").build()
        );

        ArchitectureAnalysisResponse response = ArchitectureAnalysisResponse.from(components);

        assertThat(response).isNotNull();
        assertThat(response.getPatterns()).isNotEmpty();
        assertThat(response.getComplexity()).isEqualTo("Baixa");
    }
}
