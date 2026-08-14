package com.trophix.api.auth.application.usecases;

import com.trophix.api.auth.application.ports.in.LogoutUseCase;
import com.trophix.api.auth.application.ports.out.OpaqueTokenPort;
import com.trophix.api.auth.application.ports.out.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Revokes the whole refresh token family of the presented token, invalidating
 * the session server-side. Idempotent: an absent or already-revoked token is a
 * no-op (the client clears the cookies regardless).
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class LogoutUseCaseImpl implements LogoutUseCase {

    private final RefreshTokenRepository refreshTokenRepository;
    private final OpaqueTokenPort opaqueToken;

    @Override
    @Transactional
    public void logout(LogoutCommand command) {
        if (command.refreshToken() == null || command.refreshToken().isBlank()) {
            return;
        }
        String tokenHash = opaqueToken.hash(command.refreshToken());
        refreshTokenRepository.findByHash(tokenHash)
                .ifPresent(token -> {
                    int revoked = refreshTokenRepository.revokeFamily(token.familyId());
                    log.info("Logout: família revogada family={} userId={} tokensRevogados={}",
                            token.familyId(), token.userId(), revoked);
                });
    }
}
