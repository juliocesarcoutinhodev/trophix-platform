package com.trophix.api.games.model;

import com.trophix.api.shared.domain.UuidV7;

import java.util.UUID;

/**
 * Game domain entity. Pure Java, no framework annotations.
 */
public record Game(
        UUID id,
        String npCommunicationId,
        String name,
        String imageUrl,
        String platform,
        Integer totalTrophies,
        boolean featured) {

    public static Game create(String npCommunicationId, String name, String imageUrl,
                              String platform, Integer totalTrophies) {
        return new Game(UuidV7.generate(), npCommunicationId, name, imageUrl, platform, totalTrophies, false);
    }

    public Game withFeatured(boolean featured) {
        return new Game(id, npCommunicationId, name, imageUrl, platform, totalTrophies, featured);
    }
}