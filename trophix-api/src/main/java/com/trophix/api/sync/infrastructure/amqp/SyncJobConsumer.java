package com.trophix.api.sync.infrastructure.amqp;

import com.trophix.api.sync.domain.SyncJob;
import com.trophix.api.shared.exception.BusinessException;
import com.trophix.api.shared.exception.CircuitOpenException;
import com.trophix.api.shared.exception.PsnServiceException;
import com.trophix.api.shared.exception.ResourceNotFoundException;
import com.trophix.api.trophies.application.ports.in.SyncGameTrophiesUseCase;
import com.trophix.api.users.application.async.UserProfileSyncExecutor;
import com.trophix.api.users.application.ports.out.UserRepository;
import com.trophix.api.users.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

/**
 * Consumes async sync jobs from the queue. Transient sidecar failures
 * (PsnServiceException / CircuitOpenException) propagate so the broker retries
 * and eventually dead-letters; permanent errors (missing user/game, no
 * accountId) are logged and skipped without retry.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SyncJobConsumer {

    private static final Duration SYNC_COOLDOWN = Duration.ofMinutes(15);

    private final UserRepository userRepository;
    private final UserProfileSyncExecutor profileSyncExecutor;
    private final SyncGameTrophiesUseCase syncGameTrophiesUseCase;

    @RabbitListener(queues = SyncQueueConfig.SYNC_QUEUE)
    public void onMessage(SyncJob job) {
        try {
            switch (job.type()) {
                case PROFILE_SYNC -> processProfileSync(job.userId());
                case TROPHY_SYNC -> processTrophySync(job.userId(), job.gameId());
                case TROPHY_CATALOG_SYNC -> processCatalogSync(job.gameId());
            }
        } catch (PsnServiceException | CircuitOpenException ex) {
            throw ex;
        } catch (BusinessException | ResourceNotFoundException ex) {
            log.warn("Job ignorado (erro permanente): type={} userId={} gameId={} -> {}",
                    job.type(), job.userId(), job.gameId(), ex.getMessage());
        }
    }

    private void processProfileSync(UUID userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            log.warn("Job de perfil ignorado: userId={} não existe mais", userId);
            return;
        }
        if (user.isInSyncCooldown(SYNC_COOLDOWN, Instant.now())) {
            log.info("Job de perfil ignorado: userId={} ainda em cooldown", userId);
            return;
        }
        profileSyncExecutor.syncProfile(userId);
    }

    private void processTrophySync(UUID userId, UUID gameId) {
        syncGameTrophiesUseCase.sync(userId, gameId);
    }

    private void processCatalogSync(UUID gameId) {
        syncGameTrophiesUseCase.syncCatalog(gameId);
    }
}
