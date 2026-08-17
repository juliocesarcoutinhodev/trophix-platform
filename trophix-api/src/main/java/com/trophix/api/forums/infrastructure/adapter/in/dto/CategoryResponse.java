package com.trophix.api.forums.infrastructure.adapter.in.dto;

import java.time.Instant;
import java.util.UUID;

public record CategoryResponse(
        UUID id,
        String name,
        String description,
        int orderIndex,
        long topicsCount,
        String lastTopicId,
        String lastTopicTitle,
        String lastTopicAuthor,
        Instant lastTopicUpdatedAt) {
}
