package com.trophix.api.games.model;

import com.trophix.api.shared.domain.UuidV7;

import java.time.Instant;
import java.util.UUID;

/**
 * Association between a User and a Game holding the user's progress.
 * Pure Java, no framework annotations.
 */
public record UserGame(
        UUID id,
        UUID userId,
        UUID gameId,
        Integer progressPercentage,
        Integer earnedTrophies,
        Instant lastPlayedAt) {

    public static UserGame create(UUID userId, UUID gameId, Integer progressPercentage,
                                  Integer earnedTrophies, Instant lastPlayedAt) {
        return new UserGame(UuidV7.generate(), userId, gameId, progressPercentage,
                earnedTrophies, lastPlayedAt);
    }
}