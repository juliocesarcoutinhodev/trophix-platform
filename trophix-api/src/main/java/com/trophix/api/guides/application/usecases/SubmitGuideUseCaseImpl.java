package com.trophix.api.guides.application.usecases;

import com.trophix.api.games.application.ports.out.GameRepositoryPort;
import com.trophix.api.guides.application.ports.in.SubmitGuideUseCase;
import com.trophix.api.guides.application.ports.out.GuideRepositoryPort;
import com.trophix.api.guides.model.Guide;
import com.trophix.api.shared.exception.BusinessException;
import com.trophix.api.shared.exception.ResourceNotFoundException;
import com.trophix.api.trophies.application.ports.out.TrophyRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class SubmitGuideUseCaseImpl implements SubmitGuideUseCase {

    private final GuideRepositoryPort guideRepository;
    private final TrophyRepositoryPort trophyRepository;
    private final GameRepositoryPort gameRepository;

    @Override
    @Transactional
    public String submit(UUID authorId, SubmitGuideCommand command) {
        if (command.content() == null || command.content().isBlank()) {
            throw new BusinessException("O conteúdo do guia não pode estar vazio.");
        }

        if (command.trophyId() == null && command.gameId() == null) {
            throw new BusinessException("Informe o troféu ou o jogo do guia.");
        }

        if (command.trophyId() != null) {
            trophyRepository.findById(command.trophyId())
                    .orElseThrow(() -> new ResourceNotFoundException("Troféu não encontrado"));
        }

        if (command.gameId() != null) {
            gameRepository.findById(command.gameId())
                    .orElseThrow(() -> new ResourceNotFoundException("Jogo não encontrado"));
        }

        Guide guide = Guide.create(command.trophyId(), command.gameId(), authorId,
                command.content().trim(), command.videoUrl());
        guideRepository.save(guide);

        log.info("Guia submetido authorId={} trophyId={} gameId={}",
                authorId, command.trophyId(), command.gameId());
        return "Guia submetido com sucesso e aguardando aprovação.";
    }
}