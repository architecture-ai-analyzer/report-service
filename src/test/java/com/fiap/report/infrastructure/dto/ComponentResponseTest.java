package com.fiap.report.infrastructure.dto;

import com.fiap.report.domain.component.ComponentData;
import com.fiap.report.domain.component.ComponentType;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ComponentResponseTest {

    @Test
    void from_mapsFieldsAndComputesCriticalityAndConnectionsCount() {
        ComponentData c = ComponentData.builder()
                .id("c1")
                .name("API Gateway")
                .type(ComponentType.API)
                .connections(List.of("svc1", "svc2"))
                .technology("Spring Boot")
                .description("gateway")
                .build();

        ComponentResponse resp = ComponentResponse.from(c);

        assertThat(resp.getId()).isEqualTo("c1");
        assertThat(resp.getName()).isEqualTo("API Gateway");
        assertThat(resp.getType()).isEqualTo("API");
        assertThat(resp.getConnections()).containsExactly("svc1", "svc2");
        assertThat(resp.getConnectionsCount()).isEqualTo(2);
        assertThat(resp.getTechnology()).isEqualTo("Spring Boot");
        assertThat(resp.getDescription()).isEqualTo("gateway");
        assertThat(resp.getCriticality()).isEqualTo("Alta");
    }
}
