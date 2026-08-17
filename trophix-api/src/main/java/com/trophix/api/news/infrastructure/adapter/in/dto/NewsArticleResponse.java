package com.trophix.api.news.infrastructure.adapter.in.dto;

import java.time.Instant;
import java.util.UUID;

/**
 * Public response for a {@code NewsArticle}.
 */
public record NewsArticleResponse(
        UUID id,
        String title,
        String link,
        String imageUrl,
        String source,
        Instant publishedAt,
        boolean isFeatured) {
}
