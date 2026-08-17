package com.trophix.api.news.model;

import java.time.Instant;

/**
 * A single raw entry parsed from a feed, before it becomes a {@link NewsArticle}.
 * Pure Java.
 */
public record NewsFeedItem(String title, String link, String imageUrl, Instant publishedAt) {
}
