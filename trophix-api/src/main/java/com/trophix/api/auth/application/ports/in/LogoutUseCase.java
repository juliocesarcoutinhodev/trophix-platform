package com.trophix.api.auth.application.ports.in;

/**
 * Ends a session server-side by revoking the whole refresh token family of the
 * presented token. Idempotent: logging out without a valid session is a no-op.
 */
public interface LogoutUseCase {

    void logout(LogoutCommand command);

    record LogoutCommand(String refreshToken) {}
}
