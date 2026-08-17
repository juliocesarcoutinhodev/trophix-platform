package com.trophix.api.sync.domain;

import java.util.UUID;

/**
 * Message contract for the asynchronous PSN synchronization queue.
 * Pure Java, serialized as JSON by the AMQP infrastructure.
 */
public record SyncJob(Type type, UUID userId, UUID gameId) {

    public enum Type { PROFILE_SYNC, TROPHY_SYNC }

    public static SyncJob profileSync(UUID userId) {
        return new SyncJob(Type.PROFILE_SYNC, userId, null);
    }

    public static SyncJob trophySync(UUID userId, UUID gameId) {
        return new SyncJob(Type.TROPHY_SYNC, userId, gameId);
    }
}
