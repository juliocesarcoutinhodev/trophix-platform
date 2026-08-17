package com.trophix.api.news.application.ports.in;

import com.trophix.api.news.model.NewsSource;

import java.util.List;

/**
 * Polls the configured RSS feeds, persists any new article and publishes a
 * {@code NewsDiscoveredEvent} for each one. Returns how many new articles
 * were saved.
 */
public interface RefreshNewsUseCase {

    int refresh(List<NewsSource> sources);
}
