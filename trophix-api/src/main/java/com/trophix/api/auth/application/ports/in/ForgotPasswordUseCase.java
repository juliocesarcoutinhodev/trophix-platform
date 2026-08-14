package com.trophix.api.auth.application.ports.in;

/**
 * Requests a password reset: issues a single-use token and e-mails a reset link
 * to the account's address. Always succeeds (generic response) to avoid
 * disclosing which e-mails are registered.
 */
public interface ForgotPasswordUseCase {

    void requestReset(ForgotPasswordCommand command);

    record ForgotPasswordCommand(String email) {}
}
