package com.trophix.api.admin.application.ports.in;

import com.trophix.api.games.model.Game;

import java.util.UUID;

/**
 * Toggles the manual highlight (featured) flag of a game (admin).
 */
public interface SetGameFeaturedUseCase {

    Game execute(SetGameFeaturedCommand command);

    record SetGameFeaturedCommand(UUID gameId, boolean isFeatured) {
    }
}
