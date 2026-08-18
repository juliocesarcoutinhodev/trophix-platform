package com.trophix.api.games.application.ports.in;

import com.trophix.api.games.model.Game;

/**
 * Imports a game from the PSN (via the sidecar): fetches its details and
 * trophy catalog, stores the cover and trophy icons in object storage and
 * persists the game with its trophies.
 */
public interface ImportGameUseCase {

    Game execute(ImportGameCommand command);

    record ImportGameCommand(String npCommunicationId) {
    }
}
