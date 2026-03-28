package com.fiap.report.infrastructure.gateway.impl;

import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageResult;
import com.fiap.report.gateway.StatusGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "aws.access.key")
public class SQSStatusGatewayImpl implements StatusGateway {

    private final AmazonSQSAsync amazonSQS;

    @Value("${aws.sqs.status-update-queue}")
    private String statusUpdateQueueUrl;

    @Override
    public void updateStatus(UUID diagramId, String status) {
        log.info("Updating status for diagram {}: {}", diagramId, status);

        try {
            String messageBody = String.format(
                "{\"diagramId\":\"%s\",\"status\":\"%s\",\"timestamp\":\"%s\"}",
                diagramId, status, java.time.Instant.now()
            );

            SendMessageRequest request = new SendMessageRequest()
                    .withQueueUrl(statusUpdateQueueUrl)
                    .withMessageBody(messageBody);

            SendMessageResult result = amazonSQS.sendMessage(request);
            log.info("Status update sent to SQS: {}", result.getMessageId());

        } catch (Exception e) {
            log.error("Error updating status for diagram: {}", diagramId, e);
            throw new RuntimeException("Failed to update status", e);
        }
    }
}
