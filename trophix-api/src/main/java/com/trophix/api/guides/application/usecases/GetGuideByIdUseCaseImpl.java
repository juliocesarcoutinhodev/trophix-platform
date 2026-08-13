package com.trophix.api.guides.application.usecases;

import com.trophix.api.guides.application.ports.in.GetGuideByIdUseCase;
import com.trophix.api.guides.application.ports.out.GuideRepositoryPort;
import com.trophix.api.guides.application.service.GuideEnricher;
import com.trophix.api.guides.model.Guide;
import com.trophix.api.guides.model.GuideListItem;
import com.trophix.api.guides.model.GuideStatus;
import com.trophix.api.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class GetGuideByIdUseCaseImpl implements GetGuideByIdUseCase {

    private final GuideRepositoryPort guideRepository;
    private final GuideEnricher guideEnricher;

    @Override
    @Transactional(readOnly = true)
    public GuideListItem getGuide(UUID guideId, UUID currentUserId) {
        Guide guide = guideRepository.findById(guideId)
                .filter(g -> g.status() == GuideStatus.APPROVED)
                .orElseThrow(() -> new ResourceNotFoundException("Guia não encontrado"));
        return guideEnricher.enrich(guide, currentUserId);
    }
}
