package com.trophix.api.trophies.application.ports.in;

import java.util.UUID;

public interface SyncGameTrophiesUseCase {

    /**
     * Synchronizes the trophy catalog of a game and the user's earned trophies.
     *
     * @param userId the authenticated user's id
     * @param gameId the game whose trophies will be synced
     * @return success message in Brazilian Portuguese
     */
    String sync(UUID userId, UUID gameId);
}