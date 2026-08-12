package com.trophix.api.users.infrastructure.scheduler;

import com.trophix.api.users.application.ports.in.SyncActiveUsersUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Driving adapter: triggers the daily automatic synchronization at 04:00.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class DailySyncScheduler {

    private final SyncActiveUsersUseCase syncActiveUsersUseCase;

    @Scheduled(cron = "0 0 4 * * *")
    public void runDailySync() {
        log.info("Iniciando sincronização automática diária (04:00).");
        syncActiveUsersUseCase.syncActiveUsers();
    }
}