package com.trophix.api.users.application.ports.out;

import com.trophix.api.users.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {

    Optional<User> findById(UUID userId);

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    /**
     * Paginated listing of users (admin), optionally filtered by a free-text
     * search on username/email (case-insensitive) and/or a specific role.
     * Null/blank filters are ignored.
     */
    Page<User> findAdminUsers(String search, String role, Pageable pageable);

    /** Counts users whose account was created at or after the given instant. */
    long countCreatedSince(Instant since);

    User save(User user);

    /** Marks the user's last sync time without rebuilding the whole entity. */
    void updateLastSyncedAt(UUID userId, Instant lastSyncedAt);

    /** Ids of users with a recent sync (proxy for "active users"). */
    List<UUID> findActiveUserIds(Instant since);

    /** Batch lookup by id, keyed by user id. Missing ids are simply absent. */
    Map<UUID, User> findAllByIds(Collection<UUID> ids);

    /**
     * Hunters leaderboard: users with a synced PSN profile, ordered by platinum
     * count (highest first), limited by the given pageable.
     */
    List<User> findTopHunters(Pageable pageable);
}