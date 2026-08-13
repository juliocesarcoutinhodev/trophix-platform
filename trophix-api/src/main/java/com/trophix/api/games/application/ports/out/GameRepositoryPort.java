package com.trophix.api.games.application.ports.out;

import com.trophix.api.games.model.Game;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public interface GameRepositoryPort {

    /**
     * Persists the game only when its npCommunicationId is not yet registered,
     * otherwise returns the existing game. Returns the (possibly existing) Game.
     */
    Game saveIfNotExists(Game game);

    Optional<Game> findById(UUID gameId);

    Optional<Game> findByNpCommunicationId(String npCommunicationId);

    /** Batch lookup by id, keyed by game id. Missing ids are simply absent. */
    Map<UUID, Game> findAllByIds(Collection<UUID> ids);
}