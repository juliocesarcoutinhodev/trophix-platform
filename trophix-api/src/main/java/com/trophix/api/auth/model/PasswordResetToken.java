package com.trophix.api.auth.model;

import com.trophix.api.shared.domain.UuidV7;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

/**
 * Single-use password reset token. Pure Java, no framework annotations.
 * <p>
 * The token value is a UUIDv7 sent in the reset link; only its SHA-256 hash is
 * persisted. Tokens are single-use ({@code consumedAt}), short-lived and scoped
 * to the requesting user.
 */
public record PasswordResetToken(
        UUID id,
        UUID userId,
        String tokenHash,
        Instant expiresAt,
        Instant createdAt,
        Instant consumedAt) {

    public static PasswordResetToken create(UUID userId, String tokenHash, Instant now, Duration ttl) {
        return new PasswordResetToken(UuidV7.generate(), userId, tokenHash, now.plus(ttl), now, null);
    }

    public boolean isExpired(Instant now) {
        return now.isAfter(expiresAt);
    }

    public boolean isConsumed() {
        return consumedAt != null;
    }

    /** Returns a copy marked as consumed at the given instant. */
    public PasswordResetToken consumed(Instant now) {
        return new PasswordResetToken(id, userId, tokenHash, expiresAt, createdAt, now);
    }
}
