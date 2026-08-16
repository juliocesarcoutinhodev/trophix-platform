package com.trophix.api.forums.model;

import com.trophix.api.shared.domain.UuidV7;

import java.time.Instant;
import java.util.UUID;

/**
 * Forum reply. Pure Java, no framework annotations.
 */
public record Reply(
        UUID id,
        UUID topicId,
        UUID authorId,
        String content,
        Instant createdAt) {

    public static Reply create(UUID topicId, UUID authorId, String content) {
        return new Reply(UuidV7.generate(), topicId, authorId, content, Instant.now());
    }
}
