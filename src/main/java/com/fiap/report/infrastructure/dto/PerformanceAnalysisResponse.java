package com.fiap.report.infrastructure.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PerformanceAnalysisResponse {
    private List<MetricResponse> metrics;
    private List<String> bottlenecks;
    private CapacityResponse capacity;
    
    public static PerformanceAnalysisResponse from(List<ComponentResponse> components) {
        List<MetricResponse> metrics = List.of(
            MetricResponse.builder()
                .metric("Tempo de Resposta")
                .description("Tempo médio de resposta")
                .value("250ms")
                .status("good")
                .build(),
            MetricResponse.builder()
                .metric("Throughput")
                .description("Requisições por segundo")
                .value("1000 req/s")
                .status("good")
                .build(),
            MetricResponse.builder()
                .metric("Uso de Memória")
                .description("Uso de memória")
                .value("512MB")
                .status("warning")
                .build()
        );
        
        List<String> bottlenecks = List.of(
            "Pool de conexões com banco de dados",
            "Rate limiting no API Gateway"
        );
        
        CapacityResponse capacity = CapacityResponse.builder()
                .current(1000)
                .recommended(5000)
                .maxCapacity(10000)
                .build();
        
        return PerformanceAnalysisResponse.builder()
                .metrics(metrics)
                .bottlenecks(bottlenecks)
                .capacity(capacity)
                .build();
    }
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class MetricResponse {
    private String metric;
    private String description;
    private String value;
    private String status;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class CapacityResponse {
    private int current;
    private int recommended;
    private int maxCapacity;
}
