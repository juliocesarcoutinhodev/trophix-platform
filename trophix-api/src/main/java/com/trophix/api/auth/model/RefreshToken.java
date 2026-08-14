package com.trophix.api.auth.model;

import com.trophix.api.shared.domain.UuidV7;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

/**
 * Server-side refresh session. Pure Java, no framework annotations.
 * <p>
 * The {@code tokenHash} stores the SHA-256 hash of the opaque refresh token —
 * the raw value is only ever kept in the HttpOnly cookie. Tokens that share the
 * same {@code familyId} descend from the same login; when rotation is performed
 * the previous token is revoked and a new one is issued in the same family.
 * Presenting an already-revoked token is a sign of theft and revokes the whole
 * family (reuse detection).
 */
public record RefreshToken(
        UUID id,
        UUID familyId,
        UUID userId,
        String tokenHash,
        Instant expiresAt,
        Instant createdAt,
        Instant revokedAt,
        Instant lastUsedAt,
        String userAgent,
        String ipAddress) {

    /** Creates a fresh, active token in a brand-new family (login). */
    public static RefreshToken create(UUID userId, String tokenHash, Instant now, Duration ttl,
                                      String userAgent, String ipAddress) {
        return new RefreshToken(UuidV7.generate(), UuidV7.generate(), userId, tokenHash,
                now.plus(ttl), now, null, now, userAgent, ipAddress);
    }

    /**
     * Issues the next token in the same family (rotation): the family id is kept
     * so the whole chain descends from the original login.
     */
    public RefreshToken rotateTo(String newTokenHash, Instant now, Duration ttl,
                                 String userAgent, String ipAddress) {
        return new RefreshToken(UuidV7.generate(), familyId, userId, newTokenHash,
                now.plus(ttl), now, null, now, userAgent, ipAddress);
    }

    public boolean isRevoked() {
        return revokedAt != null;
    }

    public boolean isExpired(Instant now) {
        return now.isAfter(expiresAt);
    }

    /** Whether the token sat unused for longer than the idle timeout. */
    public boolean hasBeenIdle(Duration idleTimeout, Instant now) {
        return lastUsedAt != null && now.isAfter(lastUsedAt.plus(idleTimeout));
    }

    /** Returns a copy with this token revoked at the given instant. */
    public RefreshToken revoked(Instant now) {
        return new RefreshToken(id, familyId, userId, tokenHash, expiresAt, createdAt, now,
                lastUsedAt, userAgent, ipAddress);
    }
}
