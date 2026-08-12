package com.trophix.api.guides.infrastructure.adapter.in.dto;

import java.time.Instant;
import java.util.UUID;

public record GuideResponse(
        UUID id,
        UUID trophyId,
        UUID gameId,
        UUID authorId,
        String content,
        String videoUrl,
        String status,
        Integer upvotesCount,
        Instant createdAt,
        Instant updatedAt) {
}