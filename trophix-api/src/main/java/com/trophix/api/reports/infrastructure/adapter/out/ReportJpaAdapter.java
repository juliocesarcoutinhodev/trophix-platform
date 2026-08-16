package com.trophix.api.reports.infrastructure.adapter.out;

import com.trophix.api.reports.application.ports.out.ReportRepository;
import com.trophix.api.reports.model.Report;
import com.trophix.api.reports.model.ReportStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ReportJpaAdapter implements ReportRepository {

    private final ReportSpringDataRepository springDataRepository;
    private final ReportMapper mapper;

    @Override
    @Transactional
    public Report save(Report report) {
        return mapper.toDomain(springDataRepository.save(mapper.toEntity(report)));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Report> findById(UUID reportId) {
        return springDataRepository.findById(reportId).map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Report> findByStatus(ReportStatus status, Pageable pageable) {
        return springDataRepository.findByStatus(status, pageable).map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public long countByStatus(ReportStatus status) {
        return springDataRepository.countByStatus(status);
    }

    @Override
    @Transactional(readOnly = true)
    public long countByStatusSince(ReportStatus status, Instant since) {
        return springDataRepository.countByStatusSince(status, since);
    }
}
