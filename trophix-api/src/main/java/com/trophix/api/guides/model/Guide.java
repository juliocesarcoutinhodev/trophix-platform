package com.trophix.api.guides.model;

import com.trophix.api.shared.domain.UuidV7;

import java.time.Instant;
import java.util.UUID;

/**
 * Guide domain entity. A guide targets either a trophy or a game
 * (roadmap); at least one must be present. Pure Java.
 */
public record Guide(
        UUID id,
        UUID trophyId,
        UUID gameId,
        UUID authorId,
        String title,
        String description,
        String content,
        String videoUrl,
        GuideStatus status,
        Integer upvotesCount,
        Instant createdAt,
        Instant updatedAt) {

    public static Guide create(UUID trophyId, UUID gameId, UUID authorId,
                               String title, String description, String content, String videoUrl) {
        Instant now = Instant.now();
        return new Guide(UuidV7.generate(), trophyId, gameId, authorId, title, description,
                content, videoUrl, GuideStatus.PENDING, 0, now, now);
    }

    /** Blank draft created automatically when a game is imported from the PSN. */
    public static Guide createImported(UUID trophyId, UUID gameId, UUID authorId,
                                       String title, String description, String content, String videoUrl) {
        Instant now = Instant.now();
        return new Guide(UuidV7.generate(), trophyId, gameId, authorId, title, description,
                content, videoUrl, GuideStatus.IMPORTED, 0, now, now);
    }

    /** Returns a copy with the editable fields replaced (admin edit). */
    public Guide updated(String newTitle, String newDescription, String newContent, String newVideoUrl) {
        return new Guide(id, trophyId, gameId, authorId, newTitle, newDescription, newContent,
                newVideoUrl, status, upvotesCount, createdAt, Instant.now());
    }
}