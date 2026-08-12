package com.trophix.api.users.application.ports.out;

import com.trophix.api.users.model.User;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {

    Optional<User> findById(UUID userId);

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    User save(User user);

    /** Marks the user's last sync time without rebuilding the whole entity. */
    void updateLastSyncedAt(UUID userId, Instant lastSyncedAt);

    /** Ids of users with a recent sync (proxy for "active users"). */
    List<UUID> findActiveUserIds(Instant since);
}