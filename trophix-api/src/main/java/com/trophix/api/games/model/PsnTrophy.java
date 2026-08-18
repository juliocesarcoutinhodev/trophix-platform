package com.trophix.api.games.model;

/**
 * A single catalog trophy entry fetched from the PSN sidecar during an
 * import. Pure Java.
 */
public record PsnTrophy(
        Integer psnTrophyId,
        String name,
        String description,
        String type,
        String iconUrl,
        Double rarity) {
}
