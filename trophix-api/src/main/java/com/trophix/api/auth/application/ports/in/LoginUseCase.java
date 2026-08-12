package com.trophix.api.auth.application.ports.in;

public interface LoginUseCase {

    /**
     * Authenticates the user and returns a signed JWT string.
     *
     * @throws com.trophix.api.shared.exception.BusinessException if credentials are invalid
     */
    String login(LoginCommand command);

    record LoginCommand(String email, String plainPassword) {}
}
