package com.trophix.api.reports.infrastructure.adapter.out;

import com.trophix.api.reports.model.Report;
import org.springframework.stereotype.Component;

@Component
public class ReportMapper {

    public Report toDomain(ReportJpaEntity entity) {
        return new Report(
                entity.getId(),
                entity.getReporterId(),
                entity.getTargetType(),
                entity.getTargetId(),
                entity.getReason(),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getResolvedAt());
    }

    public ReportJpaEntity toEntity(Report report) {
        ReportJpaEntity entity = new ReportJpaEntity();
        entity.setId(report.id());
        entity.setReporterId(report.reporterId());
        entity.setTargetType(report.targetType());
        entity.setTargetId(report.targetId());
        entity.setReason(report.reason());
        entity.setStatus(report.status());
        entity.setCreatedAt(report.createdAt());
        entity.setResolvedAt(report.resolvedAt());
        return entity;
    }
}
