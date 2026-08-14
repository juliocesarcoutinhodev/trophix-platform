package com.trophix.api.reports.application.ports.in;

import com.trophix.api.reports.model.Report;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Paginated list of reports still open (admin moderation queue).
 */
public interface ListOpenReportsUseCase {

    Page<Report> listOpen(Pageable pageable);
}
