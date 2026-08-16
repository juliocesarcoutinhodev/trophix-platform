package com.trophix.api.forums.infrastructure.adapter.in.dto;

import java.time.Instant;
import java.util.UUID;

public record TopicResponse(
        UUID id,
        UUID categoryId,
        UUID authorId,
        String authorName,
        String authorAvatarUrl,
        String title,
        String content,
        int viewsCount,
        int repliesCount,
        Instant createdAt,
        Instant updatedAt) {
}
