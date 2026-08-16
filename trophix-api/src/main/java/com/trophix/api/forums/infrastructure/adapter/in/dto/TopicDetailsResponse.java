package com.trophix.api.forums.infrastructure.adapter.in.dto;

import org.springframework.data.domain.Page;

import java.time.Instant;
import java.util.UUID;

public record TopicDetailsResponse(
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
        Instant updatedAt,
        Page<ReplyResponse> replies) {
}
