package com.trophix.api.trophies.application.ports.out;

import com.trophix.api.trophies.model.UserTrophy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface UserTrophyRepositoryPort {

    /**
     * Inserts or updates the user's earned trophies (upsert by user+trophy).
     */
    void saveAll(List<UserTrophy> userTrophies);

    /**
     * Returns the earned date (if any) of the given trophies for a user,
     * keyed by trophy id.
     */
    Map<UUID, Instant> findEarnedAtByUserIdAndTrophyIds(UUID userId, List<UUID> trophyIds);

    /** Global activity feed: recently earned trophies, newest first. */
    Page<UserTrophy> findRecentEarned(Pageable pageable);
}