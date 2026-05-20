package com.fiap.report.domain.component;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ComponentDataTest {

    @Test
    void builder_createsValidComponentData() {
        ComponentData component = ComponentData.builder()
                .id("comp-1")
                .name("API Gateway")
                .type(ComponentType.API)
                .connections(List.of("user-service", "order-service"))
                .properties(Map.of("protocol", "HTTP"))
                .technology("Spring Boot")
                .description("Main API Gateway")
                .build();

        assertThat(component).isNotNull();
        assertThat(component.getId()).isEqualTo("comp-1");
        assertThat(component.getName()).isEqualTo("API Gateway");
        assertThat(component.getType()).isEqualTo(ComponentType.API);
        assertThat(component.getConnections()).containsExactly("user-service", "order-service");
        assertThat(component.getTechnology()).isEqualTo("Spring Boot");
    }

    @Test
    void builder_withNullValues_createsComponentData() {
        ComponentData component = ComponentData.builder()
                .id("comp-1")
                .name("Service")
                .type(ComponentType.MICROSERVICE)
                .build();

        assertThat(component).isNotNull();
        assertThat(component.getId()).isEqualTo("comp-1");
        assertThat(component.getConnections()).isNull();
        assertThat(component.getProperties()).isNull();
    }
}
