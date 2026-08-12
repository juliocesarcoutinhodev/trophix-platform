package com.trophix.api.users.infrastructure.adapter.in.dto;

import java.time.Instant;
import java.util.UUID;

public record UserGameResponse(
        UUID gameId,
        String name,
        String imageUrl,
        String platform,
        Integer progressPercentage,
        Integer earnedTrophies,
        Integer totalTrophies,
        Instant lastPlayedAt) {
}