package com.trophix.api.forums.infrastructure.adapter.in.dto;

import java.time.Instant;
import java.util.UUID;

public record ReplyResponse(
        UUID id,
        UUID topicId,
        UUID authorId,
        String authorName,
        String authorAvatarUrl,
        String content,
        Instant createdAt) {
}
