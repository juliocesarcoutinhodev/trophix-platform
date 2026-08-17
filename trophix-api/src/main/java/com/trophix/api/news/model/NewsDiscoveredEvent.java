package com.trophix.api.news.model;

/**
 * Domain event published (strictly locally, via Spring Modulith) whenever a
 * brand new {@link NewsArticle} is actually persisted, so interested listeners
 * (e.g. web push, e-mail, analytics) react without the news module knowing them.
 * Pure Java.
 */
public record NewsDiscoveredEvent(NewsArticle article) {
}
