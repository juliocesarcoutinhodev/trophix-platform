package com.trophix.api.games.application.ports.out;

import com.trophix.api.games.model.UserGame;
import com.trophix.api.games.model.UserGameSummary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface UserGameRepositoryPort {

    /**
     * Inserts or updates the user's progress for a game (upsert by user+game).
     */
    void saveOrUpdate(UserGame userGame);

    /** Returns the user's progress for a specific game (with game metadata). */
    Optional<UserGameSummary> findByUserIdAndGameId(UUID userId, UUID gameId);

    /**
     * Returns the games played by the user (with game metadata), paginated
     * and ordered by most recently played.
     */
    Page<UserGameSummary> findByUserId(UUID userId, Pageable pageable);
}