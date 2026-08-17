package com.trophix.api.trophies.model;

import java.time.Instant;
import java.util.UUID;

/**
 * Read model for the global activity feed: who earned which trophy recently,
 * enriched with the author and the game. Pure Java.
 */
public record ActivityFeedItem(
        UUID userId,
        String username,
        String avatar,
        UUID trophyId,
        String trophyName,
        String trophyIconUrl,
        String gameName,
        Instant earnedAt) {
}
