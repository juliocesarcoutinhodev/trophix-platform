package com.trophix.api.games.model;

import java.util.UUID;

/**
 * Domain event published when a game is imported from the PSN, so interested
 * listeners (e.g. the guides module, which creates the blank draft) react
 * asynchronously without the games module knowing them. Carries only ids to
 * keep the Modulith event journal entry small. Pure Java.
 */
public record GameImportedEvent(
        UUID gameId,
        UUID adminId) {
}
