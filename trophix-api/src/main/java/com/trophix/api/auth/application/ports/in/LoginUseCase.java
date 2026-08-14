package com.trophix.api.auth.application.ports.in;

public interface LoginUseCase {

    /**
     * Authenticates the user and returns a new access/refresh token pair.
     * A refresh token family is created server-side on every login.
     *
     * @throws com.trophix.api.shared.exception.BusinessException if credentials are invalid
     */
    AuthTokens login(LoginCommand command);

    record LoginCommand(String email, String plainPassword, String ipAddress, String userAgent) {}
}
