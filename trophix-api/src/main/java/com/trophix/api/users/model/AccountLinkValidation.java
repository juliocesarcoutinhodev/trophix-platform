package com.trophix.api.users.model;

import java.util.UUID;

/**
 * Result of a successful account-link validation.
 * Returns the psnId, the persisted userId and a human-readable message in PT-BR.
 */
public record AccountLinkValidation(UUID userId, String psnId, String message) {
}