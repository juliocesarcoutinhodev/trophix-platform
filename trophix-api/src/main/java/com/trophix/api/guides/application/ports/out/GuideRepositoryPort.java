package com.trophix.api.guides.application.ports.out;

import com.trophix.api.guides.model.Guide;
import com.trophix.api.guides.model.GuideStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GuideRepositoryPort {

    Guide save(Guide guide);

    Optional<Guide> findById(UUID guideId);

    boolean existsById(UUID guideId);

    /** Paginated guides filtered by moderation status (admin). */
    Page<Guide> findByStatus(GuideStatus status, Pageable pageable);

    /**
     * Paginated listing of guides with optional dynamic filters (admin).
     * {@code status} filters exactly by moderation status; {@code search} is a
     * case-insensitive match on the guide title or the game name. Null/blank
     * filters are ignored. Ordered by creation date (newest first).
     */
    Page<Guide> findAll(GuideStatus status, String search, Boolean isTrophyGuide, Pageable pageable);

    /** Hard-deletes a guide (and its votes via cascade). */
    void delete(Guide guide);

    /** Total guides with the given moderation status (admin). */
    long countByStatus(GuideStatus status);

    /** Total guides with the given status created at or after the given instant. */
    long countByStatusSince(GuideStatus status, Instant since);

    List<Guide> findByTrophyIdAndStatusOrderByUpvotesCountDesc(UUID trophyId, GuideStatus status);

    List<Guide> findByGameIdAndStatusOrderByUpvotesCountDesc(UUID gameId, GuideStatus status);

    /** Whether any guide already targets the game with one of the given statuses. */
    boolean existsByGameIdAndStatusIn(UUID gameId, Collection<GuideStatus> statuses);

    /**
     * Returns the latest game roadmaps (guides without a trophy target) for
     * the given status, ordered by creation date (newest first), up to the
     * informed limit. When {@code search} is provided (case-insensitive),
     * only roadmaps whose title or game name contains the term are returned.
     */
    List<Guide> findLatestRoadmapsByStatus(GuideStatus status, String search, int limit);

    /**
     * Returns the trophy tips (guides with a trophy target, no game target)
     * authored by a user for the trophies of a game, ordered by trophy and
     * creation date.
     */
    List<Guide> findTrophyTipsByAuthorAndGame(UUID gameId, UUID authorId, GuideStatus status);

    /** Atomic counter update: avoids lost updates under concurrent voting. */
    void incrementUpvotesCount(UUID guideId);

    /** Atomic counter update, never below zero. */
    void decrementUpvotesCount(UUID guideId);

    void updateStatus(UUID guideId, GuideStatus status);
}