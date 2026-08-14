package com.trophix.api.reports.infrastructure.adapter.in;

import com.trophix.api.reports.application.ports.in.SubmitReportUseCase;
import com.trophix.api.reports.infrastructure.adapter.in.dto.SubmitReportRequest;
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

    @PostMapping
    public ResponseEntity<Void> submit(
            @AuthenticationPrincipal String userId,
            @Valid @RequestBody SubmitReportRequest request) {
        submitReportUseCase.submit(new SubmitReportUseCase.SubmitReportCommand(
                UUID.fromString(userId), request.targetType(), request.targetId(), request.reason()));
        return ResponseEntity.ok().build();
    }
}
