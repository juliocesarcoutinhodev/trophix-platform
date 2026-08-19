package com.trophix.api.guides.infrastructure.notification;

import com.trophix.api.games.application.ports.out.GameRepositoryPort;
import com.trophix.api.games.model.GameImportedEvent;
import com.trophix.api.guides.application.ports.out.GuideRepositoryPort;
import com.trophix.api.guides.model.Guide;
import com.trophix.api.guides.model.GuideStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Asynchronous, transactional listener for {@link GameImportedEvent}: creates
 * a blank draft guide ("Guia Oficial: {game}") owned by the importing admin so
 * the game shows up in the moderation queue under the Imported tab. Idempotent:
 * if the game already has a guide (IMPORTED, PENDING or APPROVED) the draft is
 * skipped, avoiding duplicates on re-imports.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class GameImportedGuideListener {

    private static final List<GuideStatus> EXISTING_STATUSES =
            List.of(GuideStatus.IMPORTED, GuideStatus.PENDING, GuideStatus.APPROVED);

    private final GameRepositoryPort gameRepository;
    private final GuideRepositoryPort guideRepository;

    @ApplicationModuleListener
    public void onGameImported(GameImportedEvent event) {
        var game = gameRepository.findById(event.gameId()).orElse(null);
        if (game == null) {
            log.warn("GameImportedEvent ignorado: jogo {} nao encontrado", event.gameId());
            return;
        }
        if (guideRepository.existsByGameIdAndStatusIn(event.gameId(), EXISTING_STATUSES)) {
            log.info("Guia draft ignorado: jogo {} ja possui guia", event.gameId());
            return;
        }

        Guide draft = Guide.createImported(
                null, game.id(), event.adminId(),
                "Guia Oficial: " + game.name(), null, "", null);
        guideRepository.save(draft);
        log.info("Guia draft criado apos importacao: guideId={} gameId={} authorId={}",
                draft.id(), game.id(), event.adminId());
    }
}
