package com.trophix.api.reports.application.usecases;

import com.trophix.api.guides.application.ports.out.GuideRepositoryPort;
import com.trophix.api.reports.application.ports.in.SubmitReportUseCase;
import com.trophix.api.reports.application.ports.out.ReportRepository;
import com.trophix.api.reports.model.Report;
import com.trophix.api.reports.model.ReportTargetType;
import com.trophix.api.shared.exception.BusinessException;
import com.trophix.api.shared.exception.ResourceNotFoundException;
import com.trophix.api.users.application.ports.out.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class SubmitReportUseCaseImpl implements SubmitReportUseCase {

    private final ReportRepository reportRepository;
    private final GuideRepositoryPort guideRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public void submit(SubmitReportCommand command) {
        assertTargetExists(command.targetType(), command.targetId());

        Report report = Report.create(command.reporterId(), command.targetType(),
                command.targetId(), command.reason(), Instant.now());
        reportRepository.save(report);

        log.info("Denúncia registrada: id={} reporter={} alvo={}/{} motivo='{}'",
                report.id(), command.reporterId(), command.targetType(), command.targetId(), command.reason());
    }

    private void assertTargetExists(ReportTargetType targetType, UUID targetId) {
        switch (targetType) {
            case GUIDE -> {
                if (!guideRepository.existsById(targetId)) {
                    throw new ResourceNotFoundException("Guia não encontrado");
                }
            }
            case USER -> {
                if (userRepository.findById(targetId).isEmpty()) {
                    throw new ResourceNotFoundException("Usuário não encontrado");
                }
            }
            // COMMENT ainda não tem entidade própria — aceito sem validação de existência.
            case COMMENT -> {
            }
            default -> throw new BusinessException("Tipo de denúncia inválido.");
        }
    }
}
