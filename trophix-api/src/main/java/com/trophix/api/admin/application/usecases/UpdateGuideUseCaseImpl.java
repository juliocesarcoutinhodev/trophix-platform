package com.trophix.api.admin.application.usecases;

import com.trophix.api.admin.application.ports.in.UpdateGuideUseCase;
import com.trophix.api.guides.application.ports.out.GuideRepositoryPort;
import com.trophix.api.guides.model.Guide;
import com.trophix.api.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Slf4j
@RequiredArgsConstructor
public class UpdateGuideUseCaseImpl implements UpdateGuideUseCase {

    private final GuideRepositoryPort guideRepository;

    @Override
    @Transactional
    public Guide update(UpdateGuideCommand command) {
        Guide guide = guideRepository.findById(command.guideId())
                .orElseThrow(() -> new ResourceNotFoundException("Guia não encontrado"));

        Guide updated = guide.updated(command.title(), command.description(),
                command.content(), command.videoUrl());
        Guide saved = guideRepository.save(updated);

        log.info("Guia atualizado pelo admin: guideId={} título='{}'", saved.id(), saved.title());
        return saved;
    }
}
