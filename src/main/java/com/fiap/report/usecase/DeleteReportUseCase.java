package com.fiap.report.usecase;

import java.util.UUID;

public interface DeleteReportUseCase {
    void execute(UUID reportId);
}
