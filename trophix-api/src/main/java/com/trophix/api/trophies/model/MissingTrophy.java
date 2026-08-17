package com.trophix.api.trophies.model;

import java.util.UUID;

/**
 * Read model for the "missing trophies" tab: a trophy from one of the user's
 * games that the user has not earned yet, enriched with the game name. Pure Java.
 */
public record MissingTrophy(
        UUID id,
        String name,
        String description,
        String type,
        String iconUrl,
        Double rarity,
        String gameName) {
}
