package com.trophix.api.trophies.infrastructure.adapter.out;

import com.trophix.api.games.application.ports.out.TrophyCatalogPort;
import com.trophix.api.games.model.PsnTrophy;
import com.trophix.api.trophies.application.ports.out.TrophyRepositoryPort;
import com.trophix.api.trophies.model.Trophy;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

/**
 * Implements the games module's {@link TrophyCatalogPort} so the admin game
 * import can persist trophies without creating a games &harr; trophies cycle.
 */
@Component
public class GameTrophyCatalogAdapter implements TrophyCatalogPort {

    private final TrophyRepositoryPort trophyRepository;

    public GameTrophyCatalogAdapter(TrophyRepositoryPort trophyRepository) {
        this.trophyRepository = trophyRepository;
    }

    @Override
    public int saveCatalog(UUID gameId, List<PsnTrophy> trophies) {
        return trophyRepository.saveAllIfNotExists(gameId, trophies.stream()
                        .map(t -> Trophy.create(gameId, t.psnTrophyId(), t.name(), t.description(),
                                t.type(), t.iconUrl(), t.rarity()))
                        .toList())
                .size();
    }
}
