package com.fiap.report.infrastructure.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JacksonConfigTest {

    @Test
    void objectMapper_isConfiguredWithJavaTimeModuleAndOptions() {
        JacksonConfig cfg = new JacksonConfig();
        ObjectMapper mapper = cfg.objectMapper();

        assertThat(mapper).isNotNull();
        // verify that the mapper serializes java.time types as ISO strings (not timestamps)
        try {
            String serialized = mapper.writeValueAsString(java.time.LocalDateTime.of(2020,1,2,3,4,5));
            assertThat(serialized).contains("2020-01-02");
            assertThat(mapper.getSerializationConfig().isEnabled(com.fasterxml.jackson.databind.SerializationFeature.INDENT_OUTPUT)).isTrue();
            assertThat(mapper.getSerializationConfig().isEnabled(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)).isFalse();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
