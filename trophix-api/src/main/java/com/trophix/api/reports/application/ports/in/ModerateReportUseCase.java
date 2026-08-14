package com.trophix.api.reports.application.ports.in;

import java.util.UUID;

/**
 * Closes an open report as resolved or dismissed (admin).
 */
public interface ModerateReportUseCase {

    void moderate(ModerateReportCommand command);

    record ModerateReportCommand(UUID adminId, UUID reportId, ModerationAction action) {
    }

    enum ModerationAction {
        RESOLVE,
        DISMISS
    }
}
