package com.trophix.api.news.model;

import com.trophix.api.shared.domain.UuidV7;

import java.time.Instant;
import java.util.UUID;

/**
 * News article aggregated from an RSS feed. Immutable value object.
 * Pure Java, no framework annotations.
 */
public record NewsArticle(
        UUID id,
        String title,
        String link,
        String imageUrl,
        String source,
        Instant publishedAt,
        boolean isFeatured) {

    public static NewsArticle create(String title, String link, String imageUrl,
                                     String source, Instant publishedAt) {
        return new NewsArticle(UuidV7.generate(), title, link, imageUrl, source, publishedAt, false);
    }
}
