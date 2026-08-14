package com.trophix.api.auth.application.ports.out;

import com.trophix.api.auth.model.RefreshToken;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

/**
 * Persistence contract for refresh tokens (families).
 */
public interface RefreshTokenRepository {

    Optional<RefreshToken> findByHash(String tokenHash);

    /**
     * Finds the token by hash acquiring a pessimistic write lock, so concurrent
     * presentations of the same token are serialized and only one rotation wins.
     * Must run inside a transaction.
     */
    Optional<RefreshToken> findByHashForUpdate(String tokenHash);

    RefreshToken save(RefreshToken token);

    /** Revokes every non-revoked token of the family. Returns the count revoked. */
    int revokeFamily(UUID familyId);

    /** Revokes every active token of the user (used when credentials change). */
    int revokeAllForUser(UUID userId);

    /** Deletes tokens that expired before the cutoff (maintenance). */
    int deleteExpiredBefore(Instant cutoff);
}
