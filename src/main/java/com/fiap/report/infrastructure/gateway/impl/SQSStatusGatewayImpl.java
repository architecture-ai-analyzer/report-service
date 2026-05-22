package com.fiap.report.infrastructure.gateway.impl;

import com.fiap.report.gateway.StatusGateway;
import com.fiap.report.infrastructure.config.observability.TraceSupport;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;
import software.amazon.awssdk.services.sqs.model.SqsException;

import java.util.UUID;
import java.util.concurrent.CompletionException;

@Slf4j
@Service
@RequiredArgsConstructor
public class SQSStatusGatewayImpl implements StatusGateway {

    private final SqsAsyncClient sqsAsyncClient;

    @Value("${aws.sqs.status-update-queue}")
    private String statusUpdateQueueUrl;

    @Override
    public void updateStatus(UUID diagramId, String status) {
        log.info("Updating status for diagram {}: {}", diagramId, status);

        TraceSupport.tagActiveSpan("operation.type", "sqsPublish");
        TraceSupport.tagActiveSpan("diagram.id", diagramId.toString());
        TraceSupport.tagActiveSpan("status.transition", status);

        try {
            String messageBody = String.format(
                "{\"diagramId\":\"%s\",\"status\":\"%s\",\"timestamp\":\"%s\"}",
                diagramId, status, java.time.Instant.now()
            );

            SendMessageRequest request = SendMessageRequest.builder()
                    .queueUrl(statusUpdateQueueUrl)
                    .messageBody(messageBody)
                    .build();

            SendMessageResponse result = sqsAsyncClient.sendMessage(request).join();
            log.info("Status update sent to SQS: {}", result.messageId());

        } catch (CompletionException e) {
            log.error("Error updating status for diagram: {}", diagramId, e.getCause());
            throw new IllegalStateException("Failed to update status", e.getCause());
        } catch (Exception e) {
            log.error("Error updating status for diagram: {}", diagramId, e);
            throw new IllegalStateException("Failed to update status", e);
        }
    }
}
