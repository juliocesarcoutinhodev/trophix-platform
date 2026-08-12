package com.trophix.api.users.application.usecases;

import com.trophix.api.users.application.async.UserProfileSyncExecutor;
import com.trophix.api.users.application.ports.in.SyncActiveUsersUseCase;
import com.trophix.api.users.application.ports.out.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class SyncActiveUsersUseCaseImpl implements SyncActiveUsersUseCase {

    private static final Duration ACTIVE_WINDOW = Duration.ofDays(15);
    private static final long DISPATCH_DELAY_MILLIS = 1_000;

    private final UserRepository userRepository;
    private final UserProfileSyncExecutor syncExecutor;

    @Override
    public void syncActiveUsers() {
        List<UUID> activeUserIds = userRepository.findActiveUserIds(Instant.now().minus(ACTIVE_WINDOW));
        log.info("Sincronização automática: {} usuários ativos encontrados", activeUserIds.size());

        for (UUID userId : activeUserIds) {
            syncExecutor.syncProfileAsync(userId);
            try {
                Thread.sleep(DISPATCH_DELAY_MILLIS);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                log.warn("Sincronização automática interrompida.");
                return;
            }
        }
    }
}