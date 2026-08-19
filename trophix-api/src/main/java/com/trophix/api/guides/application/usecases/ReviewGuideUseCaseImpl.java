package com.trophix.api.guides.application.usecases;

import com.trophix.api.guides.application.ports.in.ReviewGuideUseCase;
import com.trophix.api.guides.application.ports.out.GuideRepositoryPort;
import com.trophix.api.guides.model.GuideStatus;
import com.trophix.api.shared.exception.BusinessException;
import com.trophix.api.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class ReviewGuideUseCaseImpl implements ReviewGuideUseCase {

    private final GuideRepositoryPort guideRepository;

    @Override
    @Transactional
    public String review(UUID adminId, UUID guideId, ReviewAction action) {
        var guide = guideRepository.findById(guideId)
                .orElseThrow(() -> new ResourceNotFoundException("Guia não encontrado"));

        if (guide.status() != GuideStatus.PENDING && guide.status() != GuideStatus.IMPORTED) {
            throw new BusinessException("Guia já foi moderado.");
        }

        GuideStatus newStatus = action == ReviewAction.APPROVE
                ? GuideStatus.APPROVED
                : GuideStatus.REJECTED;

        guideRepository.updateStatus(guideId, newStatus);
        log.info("Guia {} {} pelo admin {}", guideId, action, adminId);

        return action == ReviewAction.APPROVE
                ? "Guia aprovado com sucesso."
                : "Guia rejeitado.";
    }
}