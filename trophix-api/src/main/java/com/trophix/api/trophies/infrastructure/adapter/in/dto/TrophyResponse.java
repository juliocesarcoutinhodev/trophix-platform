package com.trophix.api.trophies.infrastructure.adapter.in.dto;

import java.util.UUID;

public record TrophyResponse(
        UUID id,
        Integer psnTrophyId,
        String name,
        String description,
        String type,
        String iconUrl) {
}