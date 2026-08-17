package com.trophix.api.shared.model;

import java.util.UUID;

/**
 * Domain entity representing an authorization role.
 * Pure Java, no framework annotations.
 */
public record Role(UUID id, String name) {
}
