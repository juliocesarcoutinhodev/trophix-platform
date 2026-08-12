package com.trophix.api.trophies.model;

import com.trophix.api.shared.domain.UuidV7;

import java.time.Instant;
import java.util.UUID;

/**
 * Association between a User and a Trophy recording when it was earned.
 * Pure Java.
 */
public record UserTrophy(
        UUID id,
        UUID userId,
        UUID trophyId,
        Instant earnedAt) {

    public static UserTrophy create(UUID userId, UUID trophyId, Instant earnedAt) {
        return new UserTrophy(UuidV7.generate(), userId, trophyId, earnedAt);
    }
}