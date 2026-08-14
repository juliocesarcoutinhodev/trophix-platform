package com.trophix.api.auth.application.usecases;

import com.trophix.api.auth.application.ports.in.ResetPasswordUseCase;
import com.trophix.api.auth.application.ports.out.OpaqueTokenPort;
import com.trophix.api.auth.application.ports.out.PasswordEncoderPort;
import com.trophix.api.auth.application.ports.out.PasswordResetRepository;
import com.trophix.api.auth.application.ports.out.RefreshTokenRepository;
import com.trophix.api.auth.model.PasswordResetToken;
import com.trophix.api.shared.exception.BusinessException;
import com.trophix.api.users.application.ports.out.UserRepository;
import com.trophix.api.users.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

/**
 * Validates the single-use reset token, updates the password and revokes every
 * session of the user (all refresh token families).
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class ResetPasswordUseCaseImpl implements ResetPasswordUseCase {

    private static final String INVALID_LINK = "Link inválido ou expirado. Solicite um novo link.";

    private final PasswordResetRepository passwordResetRepository;
    private final OpaqueTokenPort opaqueToken;
    private final UserRepository userRepository;
    private final PasswordEncoderPort passwordEncoder;
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    @Transactional
    public void resetPassword(ResetPasswordCommand command) {
        String tokenHash = opaqueToken.hash(command.token());
        Instant now = Instant.now();

        PasswordResetToken token = passwordResetRepository.findByTokenHash(tokenHash)
                .orElseThrow(() -> new BusinessException(INVALID_LINK));

        if (token.isConsumed()) {
            throw new BusinessException(INVALID_LINK);
        }
        if (token.isExpired(now)) {
            throw new BusinessException(INVALID_LINK);
        }

        User user = userRepository.findById(token.userId())
                .orElseThrow(() -> new BusinessException(INVALID_LINK));

        userRepository.save(user.withPassword(passwordEncoder.encode(command.newPassword())));
        passwordResetRepository.save(token.consumed(now));
        int revoked = refreshTokenRepository.revokeAllForUser(user.id());

        log.info("Senha redefinida para userId={} sessõesRevogadas={}", user.id(), revoked);
    }
}
