package com.trophix.api.guides.infrastructure.adapter.out;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;

public interface GuideVoteSpringDataRepository extends JpaRepository<GuideVoteEntity, UUID> {

    boolean existsByGuideIdAndUserId(UUID guideId, UUID userId);

    void deleteByGuideIdAndUserId(UUID guideId, UUID userId);

    @Query("select gv.guide.id from GuideVoteEntity gv where gv.user.id = :userId and gv.guide.id in :guideIds")
    Set<UUID> findVotedGuideIdsByUser(@Param("userId") UUID userId, @Param("guideIds") Collection<UUID> guideIds);
}