package com.trophix.api.trophies.model;

/**
 * A trophy from the game's catalog, as returned by the sidecar.
 */
public record PsnTrophy(
        Integer psnTrophyId,
        String name,
        String description,
        String type,
        String iconUrl) {
}