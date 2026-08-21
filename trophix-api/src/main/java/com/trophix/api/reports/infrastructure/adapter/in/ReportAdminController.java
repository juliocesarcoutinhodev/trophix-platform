package com.trophix.api.reports.infrastructure.adapter.in;

import com.trophix.api.reports.application.ports.in.ListOpenReportsUseCase;
import com.trophix.api.reports.application.ports.in.ModerateReportUseCase;
import com.trophix.api.reports.infrastructure.adapter.in.dto.ReportResponse;
import com.trophix.api.reports.infrastructure.adapter.in.mapper.ReportWebMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * Admin moderation of reports. Protected by the security rule
 * {@code /api/admin/**} (ROLE_ADMIN); the controller only orchestrates use cases.
 */
@RestController
@RequestMapping("/api/admin/reports")
@RequiredArgsConstructor
public class ReportAdminController {

    private final ListOpenReportsUseCase listOpenReportsUseCase;
    private final ModerateReportUseCase moderateReportUseCase;
    private final ReportWebMapper reportWebMapper;

    @GetMapping
    public ResponseEntity<Page<ReportResponse>> listOpen(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<ReportResponse> reports = listOpenReportsUseCase
                .listOpen(PageRequest.of(Math.max(page, 0), Math.min(size, 100)))
                .map(reportWebMapper::toResponse);
        return ResponseEntity.ok(reports);
    }

    @PostMapping("/{reportId}/resolve")
    public ResponseEntity<Void> resolve(
            @AuthenticationPrincipal String adminId,
            @PathVariable UUID reportId) {
        moderateReportUseCase.moderate(
                reportWebMapper.toResolveCommand(UUID.fromString(adminId), reportId));
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{reportId}/dismiss")
    public ResponseEntity<Void> dismiss(
            @AuthenticationPrincipal String adminId,
            @PathVariable UUID reportId) {
        moderateReportUseCase.moderate(
                reportWebMapper.toDismissCommand(UUID.fromString(adminId), reportId));
        return ResponseEntity.ok().build();
    }
}
