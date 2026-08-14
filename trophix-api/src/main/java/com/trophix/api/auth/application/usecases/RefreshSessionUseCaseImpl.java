package com.trophix.api.auth.application.usecases;

import com.trophix.api.auth.application.RefreshTokenPolicy;
import com.trophix.api.auth.application.ports.in.AuthTokens;
import com.trophix.api.auth.application.ports.in.RefreshSessionUseCase;
import com.trophix.api.auth.application.ports.out.OpaqueTokenPort;
import com.trophix.api.auth.application.ports.out.RefreshTokenRepository;
import com.trophix.api.auth.application.ports.out.TokenGeneratorPort;
import com.trophix.api.auth.model.RefreshToken;
import com.trophix.api.shared.exception.RefreshTokenException;
import com.trophix.api.users.application.ports.out.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

/**
 * Rotates a refresh token. The transaction never rolls back on
 * {@link RefreshTokenException}: the reuse/family revocation and idle-timeout
 * revocations must be committed even though the HTTP response is 401.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class RefreshSessionUseCaseImpl implements RefreshSessionUseCase {

    private final RefreshTokenRepository refreshTokenRepository;
    private final OpaqueTokenPort opaqueToken;
    private final UserRepository userRepository;
    private final TokenGeneratorPort tokenGenerator;
    private final RefreshTokenPolicy policy;

    @Override
    @Transactional(noRollbackFor = RefreshTokenException.class)
    public AuthTokens refresh(RefreshCommand command) {
        String tokenHash = opaqueToken.hash(command.refreshToken());
        Instant now = Instant.now();

        // Lock pessimista na linha apresentada: serializa rotações concorrentes
        // do mesmo token — apenas uma vence, a outra cai na detecção de reuso.
        RefreshToken presented = refreshTokenRepository.findByHashForUpdate(tokenHash)
                .orElseThrow(() -> new RefreshTokenException("Sessão expirada. Faça login novamente."));

        if (presented.isRevoked()) {
            int revoked = refreshTokenRepository.revokeFamily(presented.familyId());
            log.warn("Reuso de refresh token detectado: family={} userId={} tokensRevogados={} ip={}",
                    presented.familyId(), presented.userId(), revoked, command.ipAddress());
            throw new RefreshTokenException("Sessão comprometida. Faça login novamente.");
        }

        if (presented.isExpired(now)) {
            log.info("Refresh token expirado: family={} userId={}", presented.familyId(), presented.userId());
            throw new RefreshTokenException("Sessão expirada. Faça login novamente.");
        }

        if (!policy.idleTimeout().isZero() && presented.hasBeenIdle(policy.idleTimeout(), now)) {
            int revoked = refreshTokenRepository.revokeFamily(presented.familyId());
            log.info("Refresh token ocioso: family={} userId={} tokensRevogados={}",
                    presented.familyId(), presented.userId(), revoked);
            throw new RefreshTokenException("Sessão expirada por inatividade. Faça login novamente.");
        }

        // Rotação: invalida o token apresentado e emite um novo na mesma família.
        refreshTokenRepository.save(presented.revoked(now));

        String rawRefreshToken = opaqueToken.generate();
        RefreshToken newToken = presented.rotateTo(opaqueToken.hash(rawRefreshToken), now,
                policy.expiration(), command.userAgent(), command.ipAddress());
        refreshTokenRepository.save(newToken);

        var user = userRepository.findById(presented.userId())
                .orElseThrow(() -> new RefreshTokenException("Usuário não encontrado."));
        List<String> roles = user.roles().stream()
                .map(role -> role.name())
                .toList();
        String accessToken = tokenGenerator.generate(user.id().toString(), roles);

        log.info("Refresh token rotacionado: family={} userId={}", presented.familyId(), presented.userId());
        return new AuthTokens(accessToken, rawRefreshToken);
    }
}
