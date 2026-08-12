package com.trophix.api.guides.application.ports.out;

import com.trophix.api.guides.model.Guide;
import com.trophix.api.guides.model.GuideStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GuideRepositoryPort {

    Guide save(Guide guide);

    Optional<Guide> findById(UUID guideId);

    boolean existsById(UUID guideId);

    List<Guide> findByTrophyIdAndStatusOrderByUpvotesCountDesc(UUID trophyId, GuideStatus status);

    List<Guide> findByGameIdAndStatusOrderByUpvotesCountDesc(UUID gameId, GuideStatus status);

    /** Atomic counter update: avoids lost updates under concurrent voting. */
    void incrementUpvotesCount(UUID guideId);

    /** Atomic counter update, never below zero. */
    void decrementUpvotesCount(UUID guideId);

    void updateStatus(UUID guideId, GuideStatus status);
}