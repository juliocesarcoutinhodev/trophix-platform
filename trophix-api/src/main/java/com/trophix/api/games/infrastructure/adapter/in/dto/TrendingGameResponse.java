package com.trophix.api.games.infrastructure.adapter.in.dto;

import java.util.UUID;

/**
 * Hybrid trending payload expected by the frontend home page.
 */
public record TrendingGameResponse(
        UUID id,
        String name,
        String imageUrl,
        long guidesCount) {
}
