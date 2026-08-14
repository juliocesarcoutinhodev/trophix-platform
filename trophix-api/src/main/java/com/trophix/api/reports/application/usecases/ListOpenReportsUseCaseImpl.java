package com.trophix.api.reports.application.usecases;

import com.trophix.api.reports.application.ports.in.ListOpenReportsUseCase;
import com.trophix.api.reports.application.ports.out.ReportRepository;
import com.trophix.api.reports.model.Report;
import com.trophix.api.reports.model.ReportStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ListOpenReportsUseCaseImpl implements ListOpenReportsUseCase {

    private final ReportRepository reportRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<Report> listOpen(Pageable pageable) {
        return reportRepository.findByStatus(ReportStatus.OPEN, pageable);
    }
}
