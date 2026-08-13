package com.trophix.api.games.application.ports.in;

import com.trophix.api.games.model.GameDetail;

import java.util.UUID;

public interface GetGameDetailUseCase {

    /**
     * Returns the authenticated user's detail for a game (metadata + progress
     * + trophy counts per rarity) or throws when the game is not in the
     * user's catalog.
     */
    GameDetail getGameDetail(UUID userId, UUID gameId);
}
