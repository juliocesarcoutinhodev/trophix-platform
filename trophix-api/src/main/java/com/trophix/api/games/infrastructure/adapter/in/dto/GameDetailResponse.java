package com.trophix.api.games.infrastructure.adapter.in.dto;

import java.util.UUID;

public record GameDetailResponse(
        UUID gameId,
        String name,
        String imageUrl,
        String platform,
        Integer progressPercentage,
        Integer earnedTrophies,
        Integer totalTrophies,
        RarityResponse rarity) {

    public record RarityResponse(int platinum, int gold, int silver, int bronze) {
    }
}
