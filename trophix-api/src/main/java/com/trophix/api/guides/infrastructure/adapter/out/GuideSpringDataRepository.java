package com.trophix.api.guides.infrastructure.adapter.out;

import com.trophix.api.guides.model.GuideStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface GuideSpringDataRepository extends JpaRepository<GuideEntity, UUID> {

    List<GuideEntity> findByTrophyIdAndStatusOrderByUpvotesCountDesc(UUID trophyId, GuideStatus status);

    List<GuideEntity> findByGameIdAndStatusOrderByUpvotesCountDesc(UUID gameId, GuideStatus status);

    @Modifying
    @Query("update GuideEntity g set g.upvotesCount = g.upvotesCount + 1 where g.id = :id")
    void incrementUpvotesCount(@Param("id") UUID id);

    @Modifying
    @Query("update GuideEntity g set g.upvotesCount = g.upvotesCount - 1 where g.id = :id and g.upvotesCount > 0")
    void decrementUpvotesCount(@Param("id") UUID id);

    @Modifying
    @Query("update GuideEntity g set g.status = :status, g.updatedAt = CURRENT_TIMESTAMP where g.id = :id")
    void updateStatus(@Param("id") UUID id, @Param("status") GuideStatus status);
}