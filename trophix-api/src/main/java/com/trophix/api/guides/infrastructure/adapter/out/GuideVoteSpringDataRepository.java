package com.trophix.api.guides.infrastructure.adapter.out;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface GuideVoteSpringDataRepository extends JpaRepository<GuideVoteEntity, UUID> {

    boolean existsByGuideIdAndUserId(UUID guideId, UUID userId);

    void deleteByGuideIdAndUserId(UUID guideId, UUID userId);
}