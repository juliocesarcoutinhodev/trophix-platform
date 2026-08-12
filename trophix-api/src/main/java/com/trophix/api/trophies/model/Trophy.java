package com.trophix.api.trophies.model;

import com.trophix.api.shared.domain.UuidV7;

import java.util.UUID;

/**
 * Trophy domain entity (catalog entry of a game). Pure Java.
 */
public record Trophy(
        UUID id,
        UUID gameId,
        Integer psnTrophyId,
        String name,
        String description,
        String type,
        String iconUrl) {

    public static Trophy create(UUID gameId, Integer psnTrophyId, String name,
                                String description, String type, String iconUrl) {
        return new Trophy(UuidV7.generate(), gameId, psnTrophyId, name, description, type, iconUrl);
    }
}