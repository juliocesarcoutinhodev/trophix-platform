package com.trophix.api.users.application.ports.in;

public interface SyncActiveUsersUseCase {

    /**
     * Finds recently active users and dispatches their profile sync in a
     * paced manner. Used by the daily scheduler.
     */
    void syncActiveUsers();
}