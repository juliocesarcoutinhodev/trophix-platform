package com.trophix.api.ai.application.usecases;

import com.trophix.api.ai.application.ports.in.GenerateGuideAiUseCase;
import com.trophix.api.ai.application.ports.out.AiGuideGeneratorPort;
import com.trophix.api.ai.domain.GuideAiJob;
import com.trophix.api.ai.domain.GuideAiPrompt;
import com.trophix.api.games.application.ports.out.GameRepositoryPort;
import com.trophix.api.games.model.Game;
import com.trophix.api.guides.application.ports.out.GuideRepositoryPort;
import com.trophix.api.guides.model.Guide;
import com.trophix.api.trophies.application.ports.out.TrophyRepositoryPort;
import com.trophix.api.trophies.model.Trophy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Orchestrates an AI generation job: loads the guide and its target (game and,
 * for trophy tips, the trophy), asks the LLM through the {@link AiGuideGeneratorPort}
 * and persists the generated content. The guide moderation status (IMPORTED /
 * PENDING) is preserved — an admin reviews before approving.
 *
 * <p>When the LLM throws or returns an empty response, the guide content is
 * updated with a failure message instead of leaving the job hanging. That way
 * the frontend polling sees the content change immediately and stops the
 * infinite "Gerando conteúdo..." state. Permanent errors (missing entities,
 * mismatched trophy) are still logged and skipped.</p>
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class GenerateGuideAiUseCaseImpl implements GenerateGuideAiUseCase {

    private static final String FAILURE_CONTENT =
            "Falha na geração do conteúdo via IA. Por favor, tente novamente clicando em 'Gerar com IA'.";

    private final GuideRepositoryPort guideRepository;
    private final GameRepositoryPort gameRepository;
    private final TrophyRepositoryPort trophyRepository;
    private final AiGuideGeneratorPort aiGuideGenerator;

    @Override
    public void generate(GuideAiJob job) {
        Guide guide = guideRepository.findById(job.guideId()).orElse(null);
        if (guide == null) {
            log.warn("Job de IA ignorado: guia {} nao existe", job.guideId());
            return;
        }
        Game game = gameRepository.findById(guide.gameId()).orElse(null);
        if (game == null) {
            log.warn("Job de IA ignorado: jogo {} do guia {} nao existe", guide.gameId(), guide.id());
            return;
        }

        if (job.type() == GuideAiJob.Type.TROPHY_TIP_GENERATION) {
            generateTrophyTip(job, guide, game);
        } else {
            generateRoadmap(guide, game);
        }
    }

    private void generateRoadmap(Guide guide, Game game) {
        try {
            String content = aiGuideGenerator.generateRoadmapContent(
                    new GuideAiPrompt(game.name(), game.platform(), null, null));
            if (isBlank(content)) {
                log.warn("IA retornou conteudo vazio para roadmap guideId={}", guide.id());
                saveFailure(guide.id());
                return;
            }
            saveContent(guide, content);
            log.info("Roadmap gerado por IA: guideId={} game={}", guide.id(), game.name());
        } catch (Exception ex) {
            log.error("Falha ao gerar roadmap via IA guideId={}", guide.id(), ex);
            saveFailure(guide.id());
        }
    }

    private void generateTrophyTip(GuideAiJob job, Guide guide, Game game) {
        Trophy trophy = trophyRepository.findById(job.trophyId()).orElse(null);
        if (trophy == null || !trophy.id().equals(guide.trophyId())) {
            log.warn("Job de dica ignorado: trofeu {} nao pertence ao guia {}", job.trophyId(), guide.id());
            return;
        }
        try {
            String content = aiGuideGenerator.generateTrophyTipContent(
                    new GuideAiPrompt(game.name(), game.platform(), trophy.name(), trophy.description()));
            if (isBlank(content)) {
                log.warn("IA retornou conteudo vazio para dica guideId={} trophyId={}", guide.id(), trophy.id());
                saveFailure(guide.id());
                return;
            }
            saveContent(guide, content);
            log.info("Dica gerada por IA: guideId={} trophy={}", guide.id(), trophy.name());
        } catch (Exception ex) {
            log.error("Falha ao gerar dica via IA guideId={} trophyId={}", guide.id(), trophy.id(), ex);
            saveFailure(guide.id());
        }
    }

    private void saveContent(Guide guide, String content) {
        Guide updated = guide.updated(guide.title(), guide.description(), content, guide.videoUrl());
        guideRepository.save(updated);
    }

    private void saveFailure(UUID guideId) {
        guideRepository.findById(guideId).ifPresent(guide -> {
            Guide updated = guide.updated(guide.title(), guide.description(), FAILURE_CONTENT, guide.videoUrl());
            guideRepository.save(updated);
        });
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
