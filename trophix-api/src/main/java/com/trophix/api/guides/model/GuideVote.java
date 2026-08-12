package com.trophix.api.guides.model;

import com.trophix.api.shared.domain.UuidV7;

import java.time.Instant;
import java.util.UUID;

/**
 * Vote of a user on a guide. Pure Java.
 */
public record GuideVote(
        UUID id,
        UUID guideId,
        UUID userId,
        Instant votedAt) {

    public static GuideVote create(UUID guideId, UUID userId) {
        return new GuideVote(UuidV7.generate(), guideId, userId, Instant.now());
    }
}