package com.trophix.api.news.model;

/**
 * A feed source to poll: a display name and the RSS/Atom URL.
 * Pure Java.
 */
public record NewsSource(String name, String url) {
}
