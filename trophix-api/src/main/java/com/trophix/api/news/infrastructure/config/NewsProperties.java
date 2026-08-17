package com.trophix.api.news.infrastructure.config;

import com.trophix.api.news.model.NewsSource;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * News module configuration: the RSS sources to poll and the scheduling
 * intervals. Bound from {@code trophix.news.*}.
 */
@ConfigurationProperties(prefix = "trophix.news")
public record NewsProperties(
        List<NewsSource> sources,
        long refreshIntervalMs,
        long initialDelayMs) {
}
