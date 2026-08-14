package com.trophix.api.reports.application.usecases;

import com.trophix.api.reports.application.ports.in.ModerateReportUseCase;
import com.trophix.api.reports.application.ports.out.ReportRepository;
import com.trophix.api.reports.model.Report;
import com.trophix.api.shared.exception.BusinessException;
import com.trophix.api.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Component
@Slf4j
@RequiredArgsConstructor
public class ModerateReportUseCaseImpl implements ModerateReportUseCase {

    private final ReportRepository reportRepository;

    @Override
    @Transactional
    public void moderate(ModerateReportCommand command) {
        Report report = reportRepository.findById(command.reportId())
                .orElseThrow(() -> new ResourceNotFoundException("Denúncia não encontrada"));

        if (!report.isOpen()) {
            throw new BusinessException("Denúncia já foi encerrada.");
        }

        Instant now = Instant.now();
        Report closed = command.action() == ModerationAction.RESOLVE
                ? report.resolved(now)
                : report.dismissed(now);
        reportRepository.save(closed);

        log.info("Denúncia {} como {} pelo admin {} (reportId={})",
                command.action(), command.action() == ModerationAction.RESOLVE ? "resolvida" : "rejeitada",
                command.adminId(), command.reportId());
    }
}
