package com.trophix.api.admin.infrastructure.adapter.in.dto;

import java.time.Instant;
import java.util.UUID;

/**
 * Guide awaiting moderation, enriched with the target game and author names.
 */
public record ModerationGuideResponse(
        UUID id,
        UUID trophyId,
        UUID gameId,
        String gameName,
        String imageUrl,
        UUID authorId,
        String authorName,
        String title,
        String description,
        String content,
        String videoUrl,
        String status,
        Integer upvotesCount,
        Instant createdAt,
        Instant updatedAt) {
}
