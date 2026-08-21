package com.trophix.api.admin.application.ports.in;

import java.util.UUID;

/**
 * Dispatches a pure trophy-catalog sync of a game to the async queue. The
 * worker populates the game's trophy catalog from the PSN without relying on
 * the requesting user's PSN profile, so it works even when the admin does not
 * own the game. Endpoints call this port and return 202 immediately.
 */
public interface SyncGameCatalogUseCase {

    void syncCatalog(UUID gameId);
}
