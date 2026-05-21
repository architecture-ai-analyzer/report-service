package com.fiap.report.infrastructure.dto;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ProcessingStatusResponseTest {

    @Test
    void builder_createsValidResponse() {
        UUID id = UUID.randomUUID();
        ProcessingStatusResponse response = ProcessingStatusResponse.builder()
                .id(id)
                .status("ANALISADO")
                .progress(100)
                .currentStep("Finalizando")
                .createdAt(LocalDateTime.now())
                .build();

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(id);
        assertThat(response.getStatus()).isEqualTo("ANALISADO");
        assertThat(response.getProgress()).isEqualTo(100);
    }
}
