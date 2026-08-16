package com.trophix.api.forums.model;

import com.trophix.api.shared.domain.UuidV7;

import java.util.UUID;

/**
 * Forum category. Pure Java, no framework annotations.
 */
public record Category(UUID id, String name, String description, int orderIndex) {

    public static Category create(String name, String description, int orderIndex) {
        return new Category(UuidV7.generate(), name, description, orderIndex);
    }
}
