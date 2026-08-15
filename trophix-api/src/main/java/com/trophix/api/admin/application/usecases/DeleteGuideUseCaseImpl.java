package com.trophix.api.admin.application.usecases;

import com.trophix.api.admin.application.ports.in.DeleteGuideUseCase;
import com.trophix.api.guides.application.ports.out.GuideRepositoryPort;
import com.trophix.api.guides.model.Guide;
import com.trophix.api.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class DeleteGuideUseCaseImpl implements DeleteGuideUseCase {

    private final GuideRepositoryPort guideRepository;

    @Override
    @Transactional
    public void delete(UUID guideId) {
        Guide guide = guideRepository.findById(guideId)
                .orElseThrow(() -> new ResourceNotFoundException("Guia não encontrado"));
        guideRepository.delete(guide);
        log.info("Guia excluído pelo admin: guideId={} título='{}'", guide.id(), guide.title());
    }
}
