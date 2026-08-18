package com.trophix.api.games.model;

/**
 * Official game metadata retrieved from the PSN sidecar. Pure Java.
 */
public record PsnGameDetail(
        String name,
        String coverUrl,
        String platform,
        Integer totalTrophies) {
}
