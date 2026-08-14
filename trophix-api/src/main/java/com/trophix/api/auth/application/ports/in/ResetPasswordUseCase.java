package com.trophix.api.auth.application.ports.in;

/**
 * Completes a password reset: validates the single-use token, re-hashes the new
 * password and revokes every session of the user.
 */
public interface ResetPasswordUseCase {

    void resetPassword(ResetPasswordCommand command);

    record ResetPasswordCommand(String token, String newPassword) {}
}
