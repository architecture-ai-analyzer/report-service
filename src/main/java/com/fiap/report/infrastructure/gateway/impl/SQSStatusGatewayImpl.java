package com.fiap.report.infrastructure.gateway.impl;

import com.fiap.report.gateway.StatusGateway;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class SQSStatusGatewayImpl implements StatusGateway {

    private final QueueMessagingTemplate queueMessagingTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public void updateStatus(UUID diagramId, String status) {
        try {
            StatusUpdateMessage message = StatusUpdateMessage.builder()
                    .diagramId(diagramId)
                    .status(status)
                    .timestamp(java.time.Instant.now().toString())
                    .build();

            String messageJson = objectMapper.writeValueAsString(message);
            
            queueMessagingTemplate.convertAndSend(
                    System.getenv("STATUS_UPDATE_QUEUE"), 
                    messageJson
            );

            log.info("Status update sent for diagram {}: {}", diagramId, status);

        } catch (Exception e) {
            log.error("Error sending status update for diagram: {}", diagramId, e);
            throw new RuntimeException("Failed to send status update", e);
        }
    }
}
