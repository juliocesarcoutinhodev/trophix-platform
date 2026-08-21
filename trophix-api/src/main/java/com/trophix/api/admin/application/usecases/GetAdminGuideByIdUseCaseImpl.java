package com.trophix.api.admin.application.usecases;

import com.trophix.api.admin.application.ports.in.GetAdminGuideByIdUseCase;
import com.trophix.api.guides.application.ports.out.GuideRepositoryPort;
import com.trophix.api.guides.model.Guide;
import com.trophix.api.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class GetAdminGuideByIdUseCaseImpl implements GetAdminGuideByIdUseCase {

    private final GuideRepositoryPort guideRepository;

    @Override
    @Transactional(readOnly = true)
    public Guide getAdminGuide(UUID guideId) {
        return guideRepository.findById(guideId)
                .orElseThrow(() -> new ResourceNotFoundException("Guia não encontrado"));
    }
}
