package com.trophix.api.users.application.ports.in;

import java.util.UUID;

public interface SyncUserProfileUseCase {

    /**
     * Synchronizes the user's PSN profile summary and game history.
     *
     * @param userId the authenticated user's id
     * @return success message in Brazilian Portuguese
     */
    String sync(UUID userId);
}