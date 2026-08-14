package com.trophix.api.auth.application.ports.out;

import com.trophix.api.auth.model.PasswordResetToken;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

/**
 * Persistence contract for password reset tokens.
 */
public interface PasswordResetRepository {

    Optional<PasswordResetToken> findByTokenHash(String tokenHash);

    PasswordResetToken save(PasswordResetToken token);

    /** Deletes tokens that expired or were consumed before the cutoff (maintenance). */
    int deleteBefore(Instant cutoff);

    /** Deletes any pending token for the user (used before issuing a new one). */
    void deleteByUserId(UUID userId);
}
