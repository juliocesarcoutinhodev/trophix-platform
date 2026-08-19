package com.trophix.api.ai.application.usecases;

import com.trophix.api.ai.application.ports.in.RequestGuideAiGenerationUseCase;
import com.trophix.api.ai.application.ports.out.GuideAiJobPublisher;
import com.trophix.api.guides.application.ports.out.GuideRepositoryPort;
import com.trophix.api.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Validates the target guide exists and dispatches the job to the async
 * queue. Returns quickly so the admin endpoint can answer 202 Accepted while
 * the LLM generation runs on the worker.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class RequestGuideAiGenerationUseCaseImpl implements RequestGuideAiGenerationUseCase {

    private final GuideRepositoryPort guideRepository;
    private final GuideAiJobPublisher jobPublisher;

    @Override
    public void requestRoadmapGeneration(UUID guideId) {
        requireGuide(guideId);
        jobPublisher.publishRoadmapGeneration(guideId);
        log.info("Geracao de roadmap encaminhada para a fila: guideId={}", guideId);
    }

    @Override
    public void requestTrophyTipGeneration(UUID guideId, UUID trophyId) {
        requireGuide(guideId);
        jobPublisher.publishTrophyTipGeneration(guideId, trophyId);
        log.info("Geracao de dica de trofeu encaminhada para a fila: guideId={} trophyId={}", guideId, trophyId);
    }

    private void requireGuide(UUID guideId) {
        if (!guideRepository.existsById(guideId)) {
            throw new ResourceNotFoundException("Guia não encontrado");
        }
    }
}
