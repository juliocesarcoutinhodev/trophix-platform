package com.trophix.api.games.infrastructure.adapter.in.dto;

import java.util.UUID;

public record GameCatalogDTO(
        UUID id,
        String name,
        String coverUrl) {
}
