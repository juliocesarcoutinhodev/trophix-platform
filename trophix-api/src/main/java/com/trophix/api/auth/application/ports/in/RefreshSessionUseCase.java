package com.trophix.api.auth.application.ports.in;

/**
 * Rotates a refresh token, issuing a new access token + refresh token pair.
 * <p>
 * The presented token is invalidated (revoked) and a fresh token of the same
 * family is issued. If the presented token was already revoked, the entire
 * family is revoked and a {@link com.trophix.api.shared.exception.RefreshTokenException}
 * is thrown (theft detection).
 */
public interface RefreshSessionUseCase {

    AuthTokens refresh(RefreshCommand command);

    record RefreshCommand(String refreshToken, String ipAddress, String userAgent) {}
}
