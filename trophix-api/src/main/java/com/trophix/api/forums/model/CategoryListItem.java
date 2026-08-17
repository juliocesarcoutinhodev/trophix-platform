package com.trophix.api.forums.model;

import java.time.Instant;
import java.util.UUID;

/**
 * Category read model carrying the number of topics and the most recently
 * updated topic (used by the "last post" column). Pure Java.
 */
public record CategoryListItem(
        UUID id,
        String name,
        String description,
        int orderIndex,
        long topicsCount,
        UUID lastTopicId,
        String lastTopicTitle,
        String lastTopicAuthor,
        Instant lastTopicUpdatedAt) {
}
