package com.fiap.report.infrastructure.config;

import com.fiap.report.infrastructure.gateway.impl.SQSStatusGatewayImpl;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class GatewayConfigTest {

    @Test
    void statusGateway_returnsSameInstance() {
        SQSStatusGatewayImpl mocked = mock(SQSStatusGatewayImpl.class);
        GatewayConfig cfg = new GatewayConfig();

        var bean = cfg.statusGateway(mocked);
        assertThat(bean).isSameAs(mocked);
    }
}
