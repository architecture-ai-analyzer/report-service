package com.fiap.report.infrastructure.config;

import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;

import static org.assertj.core.api.Assertions.assertThat;

class SQSConfigTest {

    @Test
    void sqsAsyncClient_isCreated() {
        SQSConfig cfg = new SQSConfig();
        try {
            var field = SQSConfig.class.getDeclaredField("region");
            field.setAccessible(true);
            field.set(cfg, "us-east-2");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        SqsAsyncClient client = cfg.sqsAsyncClient();

        assertThat(client).isNotNull();
        assertThat(client.serviceName()).isNotNull();
    }
}
