package com.trophix.api.reports.application.ports.in;

import com.trophix.api.reports.model.ReportTargetType;

import java.util.UUID;

/**
 * Registers a content report by an authenticated user.
 */
public interface SubmitReportUseCase {

    void submit(SubmitReportCommand command);

    record SubmitReportCommand(UUID reporterId, ReportTargetType targetType, UUID targetId, String reason) {
    }
}
