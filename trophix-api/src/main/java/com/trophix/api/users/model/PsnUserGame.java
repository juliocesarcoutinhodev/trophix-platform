package com.trophix.api.users.model;

import java.time.Instant;

/**
 * A game played by the user, as returned by the sidecar's user-games endpoint.
 */
public record PsnUserGame(
        String npCommunicationId,
        String name,
        String imageUrl,
        String platform,
        Integer progress,
        Integer earnedTrophies,
        Integer totalTrophies,
        Instant lastPlayedAt) {
}