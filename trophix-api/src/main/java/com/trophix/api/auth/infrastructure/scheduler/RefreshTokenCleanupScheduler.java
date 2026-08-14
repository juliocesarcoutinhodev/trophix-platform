package com.trophix.api.auth.infrastructure.scheduler;

import com.trophix.api.auth.application.ports.out.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;

/**
 * Driving adapter: nightly purge of expired refresh tokens, keeping the table
 * bounded. Tokens are retained a few days past expiry for audit purposes.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class RefreshTokenCleanupScheduler {

    /** How long past expiry a token is kept for audit before being purged. */
    private static final Duration RETENTION = Duration.ofDays(7);

    private final RefreshTokenRepository refreshTokenRepository;

    @Scheduled(cron = "0 30 3 * * *")
    public void purgeExpiredTokens() {
        int deleted = refreshTokenRepository.deleteExpiredBefore(Instant.now().minus(RETENTION));
        log.info("Limpeza de refresh tokens: {} registros expirados removidos.", deleted);
    }
}
