package com.fiap.report.infrastructure.gateway.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SQSStatusGatewayImplTest {

    @Mock
    private SqsAsyncClient sqsAsyncClient;

    private SQSStatusGatewayImpl gateway;

    @BeforeEach
    void setUp() {
        gateway = new SQSStatusGatewayImpl(sqsAsyncClient);
        ReflectionTestUtils.setField(gateway, "statusUpdateQueueUrl", "https://queue-url");
    }

    @Test
    void updateStatus_success_sendsMessageToSQS() {
        UUID diagramId = UUID.randomUUID();
        SendMessageResponse response = SendMessageResponse.builder()
                .messageId("msg-123")
                .build();
        
        when(sqsAsyncClient.sendMessage(any(SendMessageRequest.class)))
                .thenReturn(CompletableFuture.completedFuture(response));

        gateway.updateStatus(diagramId, "ANALISADO");

        assertThat(response.messageId()).isEqualTo("msg-123");
    }

    @Test
    void updateStatus_failure_throwsException() {
        UUID diagramId = UUID.randomUUID();
        
        when(sqsAsyncClient.sendMessage(any(SendMessageRequest.class)))
                .thenReturn(CompletableFuture.failedFuture(new RuntimeException("SQS error")));

        assertThatThrownBy(() -> gateway.updateStatus(diagramId, "ANALISADO"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Failed to update status");
    }
}
