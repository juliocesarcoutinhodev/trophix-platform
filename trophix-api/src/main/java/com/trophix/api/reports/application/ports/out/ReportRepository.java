package com.trophix.api.reports.application.ports.out;

import com.trophix.api.reports.model.Report;
import com.trophix.api.reports.model.ReportStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

/**
 * Persistence contract for content reports.
 */
public interface ReportRepository {

    Report save(Report report);

    Optional<Report> findById(UUID reportId);

    Page<Report> findByStatus(ReportStatus status, Pageable pageable);

    long countByStatus(ReportStatus status);
}
