package com.trophix.api.forums.model;

import com.trophix.api.shared.domain.UuidV7;

import java.time.Instant;
import java.util.UUID;

/**
 * Forum topic. Pure Java, no framework annotations.
 */
public record Topic(
        UUID id,
        UUID categoryId,
        UUID authorId,
        String title,
        String content,
        int viewsCount,
        int repliesCount,
        Instant createdAt,
        Instant updatedAt) {

    public static Topic create(UUID categoryId, UUID authorId, String title, String content) {
        Instant now = Instant.now();
        return new Topic(UuidV7.generate(), categoryId, authorId, title, content, 0, 0, now, now);
    }

    /** Returns a copy with {@code repliesCount} incremented and {@code updatedAt} refreshed. */
    public Topic withNewReply() {
        return new Topic(id, categoryId, authorId, title, content, viewsCount, repliesCount + 1, createdAt, Instant.now());
    }

    /** Returns a copy with {@code viewsCount} incremented. */
    public Topic incrementViews() {
        return new Topic(id, categoryId, authorId, title, content, viewsCount + 1, repliesCount, createdAt, updatedAt);
    }
}
