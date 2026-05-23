package com.fiap.report.domain.component;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ComponentTypeTest {

    @Test
    void componentType_valuesContainExpectedTypes() {
        ComponentType[] types = ComponentType.values();
        
        assertThat(types).isNotEmpty();
        assertThat(types).contains(ComponentType.MICROSERVICE);
        assertThat(types).contains(ComponentType.DATABASE);
        assertThat(types).contains(ComponentType.API);
    }

    @Test
    void componentType_fromString_returnsCorrectType() {
        ComponentType type = ComponentType.valueOf("DATABASE");
        
        assertThat(type).isEqualTo(ComponentType.DATABASE);
    }

    @Test
    void componentType_getDisplayName_returnsCorrectDisplayName() {
        assertThat(ComponentType.DATABASE.getDisplayName()).isEqualTo("Database");
        assertThat(ComponentType.API.getDisplayName()).isEqualTo("API");
        assertThat(ComponentType.MICROSERVICE.getDisplayName()).isEqualTo("Microservice");
    }
}
