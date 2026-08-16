package com.trophix.api.guides.infrastructure.adapter.in.dto;

import java.time.Instant;
import java.util.UUID;

public record GuideResponse(
        UUID id,
        UUID trophyId,
        UUID gameId,
        String gameName,
        String imageUrl,
        UUID authorId,
        String authorName,
        String authorAvatarUrl,
        String title,
        String description,
        String content,
        String videoUrl,
        String status,
        Integer upvotesCount,
        boolean currentUserVoted,
        Instant createdAt,
        Instant updatedAt) {
}
