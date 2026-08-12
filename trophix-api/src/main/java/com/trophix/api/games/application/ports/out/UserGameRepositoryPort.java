package com.trophix.api.games.application.ports.out;

import com.trophix.api.games.model.UserGame;

public interface UserGameRepositoryPort {

    /**
     * Inserts or updates the user's progress for a game (upsert by user+game).
     */
    void saveOrUpdate(UserGame userGame);
}