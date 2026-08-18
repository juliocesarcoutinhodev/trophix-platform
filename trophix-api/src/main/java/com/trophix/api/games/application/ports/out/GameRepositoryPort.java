package com.trophix.api.games.application.ports.out;

import com.trophix.api.games.model.Game;
import com.trophix.api.games.model.GameSaveResult;
import com.trophix.api.games.model.TrendingGame;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface GameRepositoryPort {

    /**
     * Persists the game only when its npCommunicationId is not yet registered,
     * otherwise returns the existing game. Also reports whether it was created.
     */
    GameSaveResult saveIfNotExists(Game game);

    /** Persists the given game state (used for updates). */
    Game save(Game game);

    Optional<Game> findById(UUID gameId);

    Optional<Game> findByNpCommunicationId(String npCommunicationId);

    /** Batch lookup by id, keyed by game id. Missing ids are simply absent. */
    Map<UUID, Game> findAllByIds(Collection<UUID> ids);

    /**
     * Returns the subset of the given game ids whose trophy catalog is empty
     * (games discovered but never populated). Used to schedule proactive syncs.
     */
    Set<UUID> findGameIdsWithoutTrophies(Collection<UUID> gameIds);

    /** Public catalog: games ordered by number of owners (user_games), newest-tie by name. */
    Page<Game> findCatalog(String search, Pageable pageable);

    /** Manually featured games, most recently registered first. */
    List<TrendingGame> findFeatured(int limit);

    /** Most played games (highest number of linked players), newest first on tie. */
    List<TrendingGame> findPopular(int limit);
}