package com.fiap.report.infrastructure.repository;

import com.fiap.report.domain.report.AnalysisReport;
import com.fiap.report.domain.report.ReportStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AnalysisReportRepositoryImplTest {

    @Mock
    private SpringDataAnalysisReportRepository springRepository;

    private AnalysisReportRepositoryImpl repository;

    @BeforeEach
    void setUp() {
        repository = new AnalysisReportRepositoryImpl(springRepository);
    }

    @Test
    void save_savesReportAndReturnsDomain() {
        UUID diagramId = UUID.randomUUID();
        AnalysisReport report = AnalysisReport.create(diagramId, "user-123");
        
        AnalysisReportEntity entity = AnalysisReportEntity.fromDomain(report);
        when(springRepository.save(any(AnalysisReportEntity.class))).thenReturn(entity);

        AnalysisReport result = repository.save(report);

        assertThat(result).isNotNull();
        verify(springRepository, times(1)).save(any(AnalysisReportEntity.class));
    }

    @Test
    void findById_existingId_returnsOptional() {
        UUID id = UUID.randomUUID();
        AnalysisReportEntity entity = AnalysisReportEntity.builder()
                .id(id)
                .diagramId(UUID.randomUUID())
                .userId("user-123")
                .status(ReportStatus.ANALISADO)
                .build();
        
        when(springRepository.findById(id)).thenReturn(Optional.of(entity));

        Optional<AnalysisReport> result = repository.findById(id);

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(id);
    }

    @Test
    void findById_nonExistingId_returnsEmpty() {
        UUID id = UUID.randomUUID();
        when(springRepository.findById(id)).thenReturn(Optional.empty());

        Optional<AnalysisReport> result = repository.findById(id);

        assertThat(result).isEmpty();
    }

    @Test
    void findByDiagramId_existingDiagramId_returnsOptional() {
        UUID diagramId = UUID.randomUUID();
        AnalysisReportEntity entity = AnalysisReportEntity.builder()
                .id(UUID.randomUUID())
                .diagramId(diagramId)
                .userId("user-123")
                .status(ReportStatus.ANALISADO)
                .build();
        
        when(springRepository.findByDiagramId(diagramId)).thenReturn(Optional.of(entity));

        Optional<AnalysisReport> result = repository.findByDiagramId(diagramId);

        assertThat(result).isPresent();
        assertThat(result.get().getDiagramId()).isEqualTo(diagramId);
    }

    @Test
    void findAll_returnsAllReports() {
        List<AnalysisReportEntity> entities = List.of(
                AnalysisReportEntity.builder()
                        .id(UUID.randomUUID())
                        .diagramId(UUID.randomUUID())
                        .userId("user-1")
                        .status(ReportStatus.ANALISADO)
                        .build(),
                AnalysisReportEntity.builder()
                        .id(UUID.randomUUID())
                        .diagramId(UUID.randomUUID())
                        .userId("user-2")
                        .status(ReportStatus.ANALISADO)
                        .build()
        );
        
        when(springRepository.findAll()).thenReturn(entities);

        List<AnalysisReport> result = repository.findAll();

        assertThat(result).hasSize(2);
    }

    @Test
    void existsById_existingId_returnsTrue() {
        UUID id = UUID.randomUUID();
        when(springRepository.existsById(id)).thenReturn(true);

        boolean result = repository.existsById(id);

        assertThat(result).isTrue();
    }

    @Test
    void existsById_nonExistingId_returnsFalse() {
        UUID id = UUID.randomUUID();
        when(springRepository.existsById(id)).thenReturn(false);

        boolean result = repository.existsById(id);

        assertThat(result).isFalse();
    }

    @Test
    void deleteById_deletesReport() {
        UUID id = UUID.randomUUID();
        
        repository.deleteById(id);

        verify(springRepository, times(1)).deleteById(id);
    }
}
