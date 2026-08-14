package com.trophix.api.auth.application.usecases;

import com.trophix.api.auth.application.PasswordResetPolicy;
import com.trophix.api.auth.application.ports.in.ForgotPasswordUseCase;
import com.trophix.api.auth.application.ports.out.EmailSenderPort;
import com.trophix.api.auth.application.ports.out.OpaqueTokenPort;
import com.trophix.api.auth.application.ports.out.PasswordResetRepository;
import com.trophix.api.auth.model.PasswordResetToken;
import com.trophix.api.shared.domain.UuidV7;
import com.trophix.api.users.application.ports.out.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Instant;

/**
 * Issues a password reset token and e-mails the reset link. The response is
 * intentionally identical whether or not the e-mail exists (anti-enumeration).
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class ForgotPasswordUseCaseImpl implements ForgotPasswordUseCase {

    private final UserRepository userRepository;
    private final PasswordResetRepository passwordResetRepository;
    private final OpaqueTokenPort opaqueToken;
    private final EmailSenderPort emailSender;
    private final PasswordResetPolicy policy;

    @Override
    public void requestReset(ForgotPasswordCommand command) {
        userRepository.findByEmail(command.email())
                .filter(user -> user.email() != null && user.password() != null)
                .ifPresent(user -> {
                    String rawToken = UuidV7.generate().toString();
                    passwordResetRepository.deleteByUserId(user.id());
                    PasswordResetToken token = PasswordResetToken.create(
                            user.id(), opaqueToken.hash(rawToken), Instant.now(), policy.tokenTtl());
                    passwordResetRepository.save(token);

                    emailSender.sendPasswordReset(
                            user.email(), user.username(), policy.resetUrl(rawToken));
                    log.info("Link de redefinição de senha enviado para email={} expiraEm={}",
                            user.email(), token.expiresAt());
                });
    }
}
