package com.trophix.api.reports.infrastructure.adapter.in;

import com.trophix.api.reports.application.ports.in.SubmitReportUseCase;
import com.trophix.api.reports.infrastructure.adapter.in.dto.SubmitReportRequest;
import com.trophix.api.reports.infrastructure.adapter.in.mapper.ReportWebMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * Public-facing report submission (any authenticated user).
 */
@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final SubmitReportUseCase submitReportUseCase;
    private final ReportWebMapper reportWebMapper;

    @PostMapping
    public ResponseEntity<Void> submit(
            @AuthenticationPrincipal String userId,
            @Valid @RequestBody SubmitReportRequest request) {
        submitReportUseCase.submit(
                reportWebMapper.toSubmitReportCommand(UUID.fromString(userId), request));
        return ResponseEntity.ok().build();
    }
}
