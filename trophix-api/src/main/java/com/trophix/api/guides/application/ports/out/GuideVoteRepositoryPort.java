package com.trophix.api.guides.application.ports.out;

import com.trophix.api.guides.model.GuideVote;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;

public interface GuideVoteRepositoryPort {

    boolean existsByGuideIdAndUserId(UUID guideId, UUID userId);

    void save(GuideVote vote);

    void deleteByGuideIdAndUserId(UUID guideId, UUID userId);

    /** Guide ids voted by the user among the given ones (batch, avoids N+1). */
    Set<UUID> findVotedGuideIdsByUser(UUID userId, Collection<UUID> guideIds);
}