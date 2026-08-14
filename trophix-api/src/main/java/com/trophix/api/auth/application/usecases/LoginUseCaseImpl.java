package com.trophix.api.auth.application.usecases;

import com.trophix.api.auth.application.RefreshTokenPolicy;
import com.trophix.api.auth.application.ports.in.AuthTokens;
import com.trophix.api.auth.application.ports.in.LoginUseCase;
import com.trophix.api.auth.application.ports.out.OpaqueTokenPort;
import com.trophix.api.auth.application.ports.out.PasswordEncoderPort;
import com.trophix.api.auth.application.ports.out.RefreshTokenRepository;
import com.trophix.api.auth.application.ports.out.TokenGeneratorPort;
import com.trophix.api.auth.model.RefreshToken;
import com.trophix.api.shared.exception.BusinessException;
import com.trophix.api.users.application.ports.out.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class LoginUseCaseImpl implements LoginUseCase {

    private final UserRepository userRepository;
    private final PasswordEncoderPort passwordEncoder;
    private final TokenGeneratorPort tokenGenerator;
    private final OpaqueTokenPort opaqueToken;
    private final RefreshTokenRepository refreshTokenRepository;
    private final RefreshTokenPolicy policy;

    @Override
    public AuthTokens login(LoginCommand command) {
        // 1. Busca usuário por e-mail
        var user = userRepository.findByEmail(command.email())
                .orElseThrow(() -> new BusinessException("Credenciais inválidas."));

        // 2. Verifica a senha
        if (user.password() == null || !passwordEncoder.matches(command.plainPassword(), user.password())) {
            throw new BusinessException("Credenciais inválidas.");
        }

        // 3. Monta lista de roles
        List<String> roles = user.roles().stream()
                .map(role -> role.name())
                .toList();

        // 4. Access token (JWT curto) com o userId como subject (identificador estável)
        String accessToken = tokenGenerator.generate(user.id().toString(), roles);

        // 5. Refresh token opaco (família nova) — só o hash é persistido
        String rawRefreshToken = opaqueToken.generate();
        RefreshToken refreshToken = RefreshToken.create(user.id(),
                opaqueToken.hash(rawRefreshToken), Instant.now(), policy.expiration(),
                command.userAgent(), command.ipAddress());
        refreshTokenRepository.save(refreshToken);

        log.info("Login realizado para email={}", command.email());
        return new AuthTokens(accessToken, rawRefreshToken);
    }
}
