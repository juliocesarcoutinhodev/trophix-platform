package com.trophix.api.forums.model;

import java.util.UUID;

/**
 * Category read model carrying the number of topics, used by listings.
 * Pure Java.
 */
public record CategoryListItem(
        UUID id,
        String name,
        String description,
        int orderIndex,
        long topicsCount) {
}
