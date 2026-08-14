package com.trophix.api.reports.infrastructure.adapter.in.mapper;

import com.trophix.api.reports.infrastructure.adapter.in.dto.ReportResponse;
import com.trophix.api.reports.model.Report;
import org.springframework.stereotype.Component;

/**
 * Converts report application results into web responses.
 */
@Component
public class ReportWebMapper {

    public ReportResponse toResponse(Report report) {
        return new ReportResponse(
                report.id(),
                report.reporterId(),
                report.targetType(),
                report.targetId(),
                report.reason(),
                report.status(),
                report.createdAt(),
                report.resolvedAt());
    }
}
