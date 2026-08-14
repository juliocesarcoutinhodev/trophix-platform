package com.trophix.api.auth.application.ports.out;

/**
 * Generates and hashes opaque refresh tokens.
 * <p>
 * The raw token is a high-entropy random value (CSPRNG, never a JWT) used only
 * in the HttpOnly cookie; only its SHA-256 hash is persisted, so a database
 * leak does not expose usable session credentials.
 */
public interface OpaqueTokenPort {

    /** Generates a new random opaque token (CSPRNG, base64url). */
    String generate();

    /** SHA-256 hex digest of the token, used for server-side storage/lookup. */
    String hash(String token);
}
