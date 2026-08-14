package com.trophix.api.reports.infrastructure.adapter.in.dto;

import com.trophix.api.reports.model.ReportStatus;
import com.trophix.api.reports.model.ReportTargetType;

import java.time.Instant;
import java.util.UUID;

public record ReportResponse(
        UUID id,
        UUID reporterId,
        ReportTargetType targetType,
        UUID targetId,
        String reason,
        ReportStatus status,
        Instant createdAt,
        Instant resolvedAt) {
}
