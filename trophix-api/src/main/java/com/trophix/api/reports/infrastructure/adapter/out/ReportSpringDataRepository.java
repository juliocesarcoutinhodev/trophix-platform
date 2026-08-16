package com.trophix.api.reports.infrastructure.adapter.out;

import com.trophix.api.reports.model.ReportStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.UUID;

public interface ReportSpringDataRepository extends JpaRepository<ReportJpaEntity, UUID> {

    Page<ReportJpaEntity> findByStatus(ReportStatus status, Pageable pageable);

    long countByStatus(ReportStatus status);

    @Query("select count(r) from ReportJpaEntity r where r.status = :status and r.createdAt >= :since")
    long countByStatusSince(@Param("status") ReportStatus status, @Param("since") Instant since);
}
