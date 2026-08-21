package com.trophix.api.reports.infrastructure.adapter.in.mapper;

import com.trophix.api.reports.application.ports.in.ModerateReportUseCase;
import com.trophix.api.reports.application.ports.in.SubmitReportUseCase;
import com.trophix.api.reports.infrastructure.adapter.in.dto.ReportResponse;
import com.trophix.api.reports.infrastructure.adapter.in.dto.SubmitReportRequest;
import com.trophix.api.reports.model.Report;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Converts report application results into web responses and inbound DTOs
 * into commands, keeping the controller free of manual instantiation.
 */
@Component
public class ReportWebMapper {

    public SubmitReportUseCase.SubmitReportCommand toSubmitReportCommand(
            UUID reporterId, SubmitReportRequest request) {
        return new SubmitReportUseCase.SubmitReportCommand(
                reporterId, request.targetType(), request.targetId(), request.reason());
    }

    public ModerateReportUseCase.ModerateReportCommand toResolveCommand(UUID adminId, UUID reportId) {
        return new ModerateReportUseCase.ModerateReportCommand(
                adminId, reportId, ModerateReportUseCase.ModerationAction.RESOLVE);
    }

    public ModerateReportUseCase.ModerateReportCommand toDismissCommand(UUID adminId, UUID reportId) {
        return new ModerateReportUseCase.ModerateReportCommand(
                adminId, reportId, ModerateReportUseCase.ModerationAction.DISMISS);
    }

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
