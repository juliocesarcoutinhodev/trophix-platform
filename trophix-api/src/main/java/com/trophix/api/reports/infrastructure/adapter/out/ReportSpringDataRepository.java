package com.trophix.api.reports.infrastructure.adapter.out;

import com.trophix.api.reports.model.ReportStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ReportSpringDataRepository extends JpaRepository<ReportJpaEntity, UUID> {

    Page<ReportJpaEntity> findByStatus(ReportStatus status, Pageable pageable);

    long countByStatus(ReportStatus status);
}
