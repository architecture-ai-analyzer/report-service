package com.fiap.report.application.impl;

import com.fiap.report.domain.report.AnalysisReport;
import com.fiap.report.gateway.AnalysisReportGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ListReportsUseCaseImplTest {

    private AnalysisReportGateway repository;
    private ListReportsUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        repository = mock(AnalysisReportGateway.class);
        useCase = new ListReportsUseCaseImpl(repository);
    }

    @Test
    void execute_returnsPagedReports() {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        
        AnalysisReport report1 = AnalysisReport.create(id1, "user1");
        AnalysisReport report2 = AnalysisReport.create(id2, "user2");
        
        List<AnalysisReport> reports = List.of(report1, report2);
        when(repository.findAll()).thenReturn(reports);

        Page<AnalysisReport> result = useCase.execute("user1", PageRequest.of(0, 10));

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getTotalElements()).isEqualTo(2);
    }

    @Test
    void execute_withPagination_returnsCorrectPage() {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        UUID id3 = UUID.randomUUID();
        
        AnalysisReport report1 = AnalysisReport.create(id1, "user1");
        AnalysisReport report2 = AnalysisReport.create(id2, "user2");
        AnalysisReport report3 = AnalysisReport.create(id3, "user3");
        
        List<AnalysisReport> reports = List.of(report1, report2, report3);
        when(repository.findAll()).thenReturn(reports);

        Page<AnalysisReport> result = useCase.execute("user1", PageRequest.of(0, 2));

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getTotalElements()).isEqualTo(3);
    }

    @Test
    void execute_emptyList_returnsEmptyPage() {
        when(repository.findAll()).thenReturn(List.of());

        Page<AnalysisReport> result = useCase.execute("user1", PageRequest.of(0, 10));

        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isEqualTo(0);
    }
}
