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
import java.util.function.Supplier;

/**
 * Orchestrates an AI generation job: loads the guide and its target (game and,
 * for trophy tips, the trophy), asks the LLM through the {@link AiGuideGeneratorPort}
 * and persists the generated content. The guide moderation status (IMPORTED /
 * PENDING) is preserved — an admin reviews before approving.
 *
 * <p>When the LLM throws or returns an empty response, the call is retried up to
 * {@link #MAX_AI_ATTEMPTS} times (Gemini's cold-start often fails only on the
 * first attempt after an idle period). If every attempt fails, the guide content
 * is updated with a failure message instead of leaving the job hanging. That way
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

    /**
     * Quantas vezes a chamada ao LLM e tentada antes de desistir. O cold-start
     * do Gemini pode retornar vazio na primeira chamada apos um periodo ocioso,
     * entao repetimos a geracao para evitar erro na cara do usuario.
     */
    private static final int MAX_AI_ATTEMPTS = 3;

    /** Pausa (ms) entre tentativas. */
    private static final long RETRY_DELAY_MS = 3_000L;

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
        GuideAiPrompt prompt = new GuideAiPrompt(game.name(), game.platform(), null, null);
        String content = callWithRetry(() -> aiGuideGenerator.generateRoadmapContent(prompt));
        if (isBlank(content)) {
            log.warn("IA nao retornou conteudo apos {} tentativas para roadmap guideId={}",
                    MAX_AI_ATTEMPTS, guide.id());
            saveFailure(guide.id());
            return;
        }
        saveContent(guide, content);
        log.info("Roadmap gerado por IA: guideId={} game={}", guide.id(), game.name());
    }

    private void generateTrophyTip(GuideAiJob job, Guide guide, Game game) {
        Trophy trophy = trophyRepository.findById(job.trophyId()).orElse(null);
        if (trophy == null || !trophy.id().equals(guide.trophyId())) {
            log.warn("Job de dica ignorado: trofeu {} nao pertence ao guia {}", job.trophyId(), guide.id());
            return;
        }
        GuideAiPrompt prompt = new GuideAiPrompt(
                game.name(), game.platform(), trophy.name(), trophy.description());
        String content = callWithRetry(() -> aiGuideGenerator.generateTrophyTipContent(prompt));
        if (isBlank(content)) {
            log.warn("IA nao retornou conteudo apos {} tentativas para dica guideId={} trophyId={}",
                    MAX_AI_ATTEMPTS, guide.id(), trophy.id());
            saveFailure(guide.id());
            return;
        }
        saveContent(guide, content);
        log.info("Dica gerada por IA: guideId={} trophy={}", guide.id(), trophy.name());
    }

    private void saveContent(Guide guide, String content) {
        Guide updated = guide.updated(guide.title(), guide.description(), content, guide.videoUrl());
        guideRepository.save(updated);
    }

    /**
     * Chama a IA ate {@code MAX_AI_ATTEMPTS} vezes com um pequeno backoff entre
     * as tentativas. Retorna o primeiro conteudo nao-vazio; {@code null} se todas
     * as tentativas falharem (excecao ou resposta vazia). O cold-start do Gemini
     * costuma falhar so na primeira chamada apos ociosidade, entao o retry resolve
     * sem expor erro ao usuario.
     */
    private String callWithRetry(Supplier<String> call) {
        String content = null;
        for (int attempt = 1; attempt <= MAX_AI_ATTEMPTS; attempt++) {
            try {
                content = call.get();
            } catch (Exception ex) {
                log.warn("Tentativa {}/{} de geracao via IA falhou: {}",
                        attempt, MAX_AI_ATTEMPTS, ex.getMessage());
                content = null;
            }
            if (!isBlank(content)) {
                return content;
            }
            if (attempt < MAX_AI_ATTEMPTS) {
                try {
                    Thread.sleep(RETRY_DELAY_MS);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    return null;
                }
            }
        }
        return content;
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
