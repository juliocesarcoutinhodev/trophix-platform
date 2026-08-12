package com.trophix.api.games.application.ports.out;

import com.trophix.api.games.model.Game;

public interface GameRepositoryPort {

    /**
     * Persists the game only when its npCommunicationId is not yet registered,
     * otherwise returns the existing game. Returns the (possibly existing) Game.
     */
    Game saveIfNotExists(Game game);
}