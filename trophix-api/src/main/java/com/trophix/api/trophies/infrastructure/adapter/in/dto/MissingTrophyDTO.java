package com.trophix.api.trophies.infrastructure.adapter.in.dto;

import java.util.UUID;

public record MissingTrophyDTO(
        UUID id,
        String name,
        String description,
        String type,
        String gameName,
        String iconUrl,
        Double rarity) {
}
