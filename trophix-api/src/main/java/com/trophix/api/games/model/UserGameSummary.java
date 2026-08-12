package com.trophix.api.games.model;

import java.time.Instant;
import java.util.UUID;

/**
 * Read model combining the user's progress for a game with the game's
 * metadata (name, image, platform and total trophies). Pure Java.
 */
public record UserGameSummary(
        UUID gameId,
        String name,
        String imageUrl,
        String platform,
        Integer progressPercentage,
        Integer earnedTrophies,
        Integer totalTrophies,
        Instant lastPlayedAt) {
}