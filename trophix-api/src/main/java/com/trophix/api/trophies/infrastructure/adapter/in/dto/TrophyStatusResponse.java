package com.trophix.api.trophies.infrastructure.adapter.in.dto;

import java.time.Instant;
import java.util.UUID;

public record TrophyStatusResponse(
        UUID id,
        Integer psnTrophyId,
        String name,
        String description,
        String type,
        String iconUrl,
        boolean earned,
        Instant earnedAt) {
}
