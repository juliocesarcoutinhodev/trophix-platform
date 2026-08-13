package com.trophix.api.games.model;

import java.util.UUID;

/**
 * Read model with the game metadata, the user's progress and the earned
 * trophy count per rarity (platinum, gold, silver, bronze). Pure Java.
 */
public record GameDetail(
        UUID gameId,
        String name,
        String imageUrl,
        String platform,
        Integer progressPercentage,
        Integer earnedTrophies,
        Integer totalTrophies,
        RarityBreakdown rarity) {

    public record RarityBreakdown(int platinum, int gold, int silver, int bronze) {
    }
}
