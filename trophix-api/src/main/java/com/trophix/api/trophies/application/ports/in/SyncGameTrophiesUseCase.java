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

    /**
     * Proactive catalog sync: fetches and persists only the game's trophy
     * catalog from the PSN (no user involved). Idempotent.
     *
     * @param gameId the game whose trophy catalog will be synced
     * @return success message in Brazilian Portuguese
     */
    String syncCatalog(UUID gameId);
}