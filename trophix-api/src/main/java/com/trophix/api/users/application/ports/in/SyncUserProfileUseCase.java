package com.trophix.api.users.application.ports.in;

import java.util.UUID;

public interface SyncUserProfileUseCase {

    /**
     * Validates the manual sync cooldown, stamps last_synced_at and
     * dispatches the heavy synchronization to the background. Returns
     * immediately (HTTP 202).
     *
     * @throws com.trophix.api.shared.exception.SyncCooldownException
     *         when the last sync happened less than the cooldown ago
     */
    void requestSync(UUID userId);
}