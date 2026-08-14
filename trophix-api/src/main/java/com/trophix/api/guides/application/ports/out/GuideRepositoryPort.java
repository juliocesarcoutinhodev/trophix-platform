package com.trophix.api.guides.application.ports.out;

import com.trophix.api.guides.model.Guide;
import com.trophix.api.guides.model.GuideStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GuideRepositoryPort {

    Guide save(Guide guide);

    Optional<Guide> findById(UUID guideId);

    boolean existsById(UUID guideId);

    /** Paginated guides filtered by moderation status (admin). */
    Page<Guide> findByStatus(GuideStatus status, Pageable pageable);

    /** Total guides with the given moderation status (admin). */
    long countByStatus(GuideStatus status);

    List<Guide> findByTrophyIdAndStatusOrderByUpvotesCountDesc(UUID trophyId, GuideStatus status);

    List<Guide> findByGameIdAndStatusOrderByUpvotesCountDesc(UUID gameId, GuideStatus status);

    /**
     * Returns the latest game roadmaps (guides without a trophy target) for
     * the given status, ordered by creation date (newest first), up to the
     * informed limit.
     */
    List<Guide> findLatestRoadmapsByStatus(GuideStatus status, int limit);

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