package com.trophix.api.shared.application.ports.out;

import java.util.UUID;

/**
 * Publishes asynchronous PSN synchronization jobs to the queue. The consumer
 * (Spring worker) executes the heavy sync without blocking the HTTP thread.
 */
public interface SyncJobPublisher {

    void publishProfileSync(UUID userId);

    void publishTrophySync(UUID userId, UUID gameId);
}
