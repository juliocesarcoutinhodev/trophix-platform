package com.trophix.api.users.model;

import java.time.Instant;
import java.util.UUID;

/**
 * Pending account-link request stored until validation.
 */
public record AccountLinkTicket(UUID id, String psnId, String verificationToken, Instant expiresAt) {
}