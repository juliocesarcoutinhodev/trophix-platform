package com.trophix.api.auth.infrastructure.scheduler;

import com.trophix.api.auth.application.ports.out.PasswordResetRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;

/**
 * Driving adapter: nightly purge of expired/consumed password reset tokens.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class PasswordResetCleanupScheduler {

    private static final Duration RETENTION = Duration.ofDays(2);

    private final PasswordResetRepository passwordResetRepository;

    @Scheduled(cron = "0 45 3 * * *")
    public void purgeExpiredTokens() {
        int deleted = passwordResetRepository.deleteBefore(Instant.now().minus(RETENTION));
        log.info("Limpeza de tokens de redefinição de senha: {} registros removidos.", deleted);
    }
}
