package com.fiap.report.domain.report;

public enum ReportStatus {
    RECEBIDO("Recebido"),
    EM_PROCESSAMENTO("Em processamento"),
    ANALISADO("Analisado"),
    ERRO("Erro");

    private final String displayName;

    ReportStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
