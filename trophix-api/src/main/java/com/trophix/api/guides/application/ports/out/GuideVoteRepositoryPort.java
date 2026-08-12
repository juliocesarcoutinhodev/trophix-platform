package com.trophix.api.guides.application.ports.out;

import com.trophix.api.guides.model.GuideVote;

import java.util.UUID;

public interface GuideVoteRepositoryPort {

    boolean existsByGuideIdAndUserId(UUID guideId, UUID userId);

    void save(GuideVote vote);

    void deleteByGuideIdAndUserId(UUID guideId, UUID userId);
}