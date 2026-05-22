package com.fiap.report.infrastructure.controller;

import com.fiap.report.domain.report.AnalysisReport;
import com.fiap.report.domain.report.ReportStatus;
import com.fiap.report.infrastructure.mapper.AIAnalysisMapper;
import com.fiap.report.infrastructure.dto.ReportResponse;
import com.fiap.report.usecase.CreateReportUseCase;
import com.fiap.report.usecase.FindReportByDiagramIdUseCase;
import com.fiap.report.usecase.GetReportUseCase;
import com.fiap.report.usecase.ListReportsUseCase;
import com.fiap.report.gateway.StatusGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class ReportControllerUnitTest {

    private CreateReportUseCase createReportUseCase;
    private FindReportByDiagramIdUseCase findReportByDiagramIdUseCase;
    private GetReportUseCase getReportUseCase;
    private ListReportsUseCase listReportsUseCase;
    private StatusGateway statusGateway;
    private AIAnalysisMapper aiAnalysisMapper;

    private ReportController controller;

    @BeforeEach
    void setUp() {
        createReportUseCase = mock(CreateReportUseCase.class);
        findReportByDiagramIdUseCase = mock(FindReportByDiagramIdUseCase.class);
        getReportUseCase = mock(GetReportUseCase.class);
        listReportsUseCase = mock(ListReportsUseCase.class);
        statusGateway = mock(StatusGateway.class);
        aiAnalysisMapper = mock(AIAnalysisMapper.class);

        controller = new ReportController(createReportUseCase, findReportByDiagramIdUseCase,
                getReportUseCase, listReportsUseCase, statusGateway, aiAnalysisMapper);
    }

    @Test
    void generateReport_whenReportExists_returnsExisting() {
        String uploadId = UUID.randomUUID().toString();
        UUID diagramId = UUID.fromString(uploadId);
        AnalysisReport existing = AnalysisReport.create(diagramId, "user1");

        when(findReportByDiagramIdUseCase.execute(diagramId)).thenReturn(Optional.of(existing));

        var resp = controller.generateReport(uploadId, Map.of("analysis", Map.of(), "metadata", Map.of()));

        assertThat(resp.getStatusCode().is2xxSuccessful()).isTrue();
        ReportResponse body = resp.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getDetectedComponents()).isNotNull();

        verify(statusGateway, never()).updateStatus(any(), any());
        verify(createReportUseCase, never()).execute(any(), any());
    }

    @Test
    void generateReport_happyPath_updatesStatusAndReturnsReport() {
        String uploadId = "upload-123";
        UUID diagramId = UUID.nameUUIDFromBytes(uploadId.getBytes());

        when(findReportByDiagramIdUseCase.execute(diagramId)).thenReturn(Optional.empty());

        AnalysisReport created = AnalysisReport.create(diagramId, "user1");
        when(createReportUseCase.execute(eq(diagramId), any())).thenReturn(created);

        var request = Map.<String,Object>of(
                "analysis", Map.of("components", java.util.List.of()),
                "metadata", Map.of("userId","user1","processingTimeMs",123)
        );

        var resp = controller.generateReport(uploadId, request);

        assertThat(resp.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(resp.getBody()).isNotNull();

        ArgumentCaptor<UUID> captor = ArgumentCaptor.forClass(UUID.class);
        verify(statusGateway, times(2)).updateStatus(captor.capture(), any());
        assertEquals(diagramId, captor.getAllValues().get(0));
    }

    @Test
    void getReport_whenException_returnsNotFound() {
        String uploadId = "not-a-uuid";
        UUID diagramId = UUID.nameUUIDFromBytes(uploadId.getBytes());

        when(getReportUseCase.execute(diagramId)).thenThrow(new RuntimeException("db"));

        var resp = controller.getReport(uploadId);

        assertThat(resp.getStatusCode().is4xxClientError()).isTrue();
    }

    @Test
    void getProcessingStatus_whenReportPresent_returnsStatus() {
        String uploadId = "upload-status";
        UUID diagramId = UUID.nameUUIDFromBytes(uploadId.getBytes());
        AnalysisReport r = AnalysisReport.create(diagramId, "u");
        r.setStatus(ReportStatus.ANALISADO);

        when(findReportByDiagramIdUseCase.execute(diagramId)).thenReturn(Optional.of(r));

        var resp = controller.getProcessingStatus(uploadId);

        assertThat(resp.getStatusCode().is2xxSuccessful()).isTrue();
        var body = resp.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getStatus()).isEqualTo("ANALISADO");
        assertThat(body.getProgress()).isEqualTo(100);
    }

    @Test
    void downloadReport_returnsPdfBytes() {
        String uploadId = "download-me";

        AnalysisReport report = AnalysisReport.builder()
            .id(UUID.randomUUID())
            .build();

        when(getReportUseCase.execute(any())).thenReturn(report);

        var resp = controller.downloadReport(uploadId, "template-tecnico");

        assertThat(resp.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(resp.getHeaders().getFirst("Content-Type")).isEqualTo("application/pdf");
        var body = resp.getBody();
        assertThat(body).isNotNull();
        String asString = new String(body);
        assertThat(asString).startsWith("%PDF-1.1");
    }
}