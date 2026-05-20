package com.fiap.report.infrastructure.config;

import com.fiap.report.gateway.StatusGateway;
import com.fiap.report.infrastructure.gateway.impl.SQSStatusGatewayImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Bean
    public StatusGateway statusGateway(SQSStatusGatewayImpl sqsStatusGateway) {
        return sqsStatusGateway;
    }
}
