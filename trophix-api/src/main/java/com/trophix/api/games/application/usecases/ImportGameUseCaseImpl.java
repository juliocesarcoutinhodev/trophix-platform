package com.trophix.api.games.application.usecases;

import com.trophix.api.games.application.ports.in.ImportGameUseCase;
import com.trophix.api.games.application.ports.out.GameImportPort;
import com.trophix.api.games.application.ports.out.GameRepositoryPort;
import com.trophix.api.games.application.ports.out.ImageStoragePort;
import com.trophix.api.games.application.ports.out.TrophyCatalogPort;
import com.trophix.api.games.model.Game;
import com.trophix.api.games.model.PsnGameDetail;
import com.trophix.api.games.model.PsnTrophy;
import com.trophix.api.shared.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public class ImportGameUseCaseImpl implements ImportGameUseCase {

    private final GameImportPort gameImportPort;
    private final TrophyCatalogPort trophyCatalogPort;
    private final ImageStoragePort imageStoragePort;
    private final GameRepositoryPort gameRepository;

    @Override
    @Transactional
    public Game execute(ImportGameCommand command) {
        String npCommunicationId = normalize(command.npCommunicationId());

        PsnGameDetail detail = gameImportPort.fetchDetails(npCommunicationId);
        List<PsnTrophy> catalog = gameImportPort.fetchTrophyCatalog(npCommunicationId);

        Optional<Game> existing = gameRepository.findByNpCommunicationId(npCommunicationId);
        String folder = "games/" + npCommunicationId;

        String coverUrl = imageStoragePort.downloadAndStore(detail.coverUrl(), folder, "cover.png");
        List<PsnTrophy> imported = catalog.stream()
                .map(t -> new PsnTrophy(
                        t.psnTrophyId(),
                        t.name(),
                        t.description(),
                        t.type(),
                        storeIcon(t, folder),
                        t.rarity()))
                .toList();

        // New games get a null id so the UuidV7 generator assigns it; the
        // persisted entity's id is then used for the trophy catalog.
        Game game = new Game(
                existing.map(Game::id).orElse(null), npCommunicationId, detail.name(), coverUrl,
                detail.platform(), detail.totalTrophies(),
                existing.map(Game::featured).orElse(false));

        Game saved = existing.isPresent()
                ? gameRepository.save(game)
                : gameRepository.insert(game);
        int savedCount = trophyCatalogPort.saveCatalog(saved.id(), imported);

        log.info("Jogo importado da PSN: gameId={} npCommunicationId={} trofeusSalvos={}",
                saved.id(), npCommunicationId, savedCount);
        return saved;
    }

    private String normalize(String npCommunicationId) {
        if (npCommunicationId == null || npCommunicationId.isBlank()) {
            throw new BusinessException("Informe o npCommunicationId do jogo.");
        }
        return npCommunicationId.trim().toUpperCase();
    }

    private String storeIcon(PsnTrophy trophy, String folder) {
        if (trophy.iconUrl() == null || trophy.iconUrl().isBlank()) {
            return null;
        }
        try {
            return imageStoragePort.downloadAndStore(trophy.iconUrl(), folder + "/trophies", "trophy-" + trophy.psnTrophyId() + ".png");
        } catch (RuntimeException ex) {
            log.warn("Nao foi possivel armazenar o icone do trofeu {} do jogo em {}; mantendo URL original.",
                    trophy.psnTrophyId(), folder, ex);
            return trophy.iconUrl();
        }
    }
}
