package com.trophix.api.trophies.application.ports.in;

import com.trophix.api.trophies.model.TrophyWithStatus;

import java.util.List;
import java.util.UUID;

public interface GetMyTrophiesUseCase {

    /**
     * Returns the game's trophy catalog with the authenticated user's
     * earning status for each trophy.
     */
    List<TrophyWithStatus> getMyTrophies(UUID userId, UUID gameId);
}
