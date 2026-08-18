package com.trophix.api.games.model;

import java.util.UUID;

/**
 * Hybrid trending result: a game promoted to the home page, either because it
 * was manually featured or because it is among the most played games. Pure
 * Java, no framework annotations.
 */
public record TrendingGame(
        UUID id,
        String name,
        String imageUrl,
        long guidesCount) {
}
