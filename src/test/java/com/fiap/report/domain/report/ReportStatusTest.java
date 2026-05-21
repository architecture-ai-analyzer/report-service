package com.fiap.report.domain.report;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ReportStatusTest {

    @Test
    void reportStatus_valuesContainExpectedStatuses() {
        ReportStatus[] statuses = ReportStatus.values();
        
        assertThat(statuses).isNotEmpty();
        assertThat(statuses).contains(ReportStatus.RECEBIDO);
        assertThat(statuses).contains(ReportStatus.EM_PROCESSAMENTO);
        assertThat(statuses).contains(ReportStatus.ANALISADO);
        assertThat(statuses).contains(ReportStatus.ERRO);
    }

    @Test
    void reportStatus_fromString_returnsCorrectStatus() {
        ReportStatus status = ReportStatus.valueOf("RECEBIDO");
        
        assertThat(status).isEqualTo(ReportStatus.RECEBIDO);
    }

    @Test
    void reportStatus_getDisplayName_returnsCorrectDisplayName() {
        assertThat(ReportStatus.RECEBIDO.getDisplayName()).isEqualTo("Recebido");
        assertThat(ReportStatus.EM_PROCESSAMENTO.getDisplayName()).isEqualTo("Em processamento");
        assertThat(ReportStatus.ANALISADO.getDisplayName()).isEqualTo("Analisado");
        assertThat(ReportStatus.ERRO.getDisplayName()).isEqualTo("Erro");
    }
}
