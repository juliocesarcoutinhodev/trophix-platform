package com.trophix.api.reports.model;

import com.trophix.api.shared.domain.UuidV7;

import java.time.Instant;
import java.util.UUID;

/**
 * Content report (guide, user or comment). Pure Java, no framework annotations.
 */
public record Report(
        UUID id,
        UUID reporterId,
        ReportTargetType targetType,
        UUID targetId,
        String reason,
        ReportStatus status,
        Instant createdAt,
        Instant resolvedAt) {

    public static Report create(UUID reporterId, ReportTargetType targetType, UUID targetId,
                                String reason, Instant now) {
        return new Report(UuidV7.generate(), reporterId, targetType, targetId, reason,
                ReportStatus.OPEN, now, null);
    }

    public boolean isOpen() {
        return status == ReportStatus.OPEN;
    }

    public Report resolved(Instant now) {
        return new Report(id, reporterId, targetType, targetId, reason,
                ReportStatus.RESOLVED, createdAt, now);
    }

    public Report dismissed(Instant now) {
        return new Report(id, reporterId, targetType, targetId, reason,
                ReportStatus.DISMISSED, createdAt, now);
    }
}
