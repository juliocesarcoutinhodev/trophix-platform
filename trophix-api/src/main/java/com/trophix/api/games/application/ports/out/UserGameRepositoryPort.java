package com.trophix.api.games.application.ports.out;

import com.trophix.api.games.model.UserGame;
import com.trophix.api.games.model.UserGameSummary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserGameRepositoryPort {

    /**
     * Inserts or updates the user's progress for a game (upsert by user+game).
     */
    void saveOrUpdate(UserGame userGame);

    /**
     * Returns the games played by the user (with game metadata), paginated
     * and ordered by most recently played.
     */
    Page<UserGameSummary> findByUsername(String username, Pageable pageable);
}