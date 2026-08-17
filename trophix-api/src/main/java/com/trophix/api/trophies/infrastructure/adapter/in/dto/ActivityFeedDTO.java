package com.trophix.api.trophies.infrastructure.adapter.in.dto;

import java.time.Instant;
import java.util.UUID;

public record ActivityFeedDTO(
        UUID userId,
        String username,
        String avatar,
        UUID trophyId,
        String trophyName,
        String trophyIconUrl,
        String gameName,
        Instant earnedAt) {
}
