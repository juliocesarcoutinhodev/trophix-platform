package com.trophix.api.guides.infrastructure.adapter.out;

import com.trophix.api.guides.model.GuideStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface GuideSpringDataRepository extends JpaRepository<GuideEntity, UUID> {

    Page<GuideEntity> findByStatus(GuideStatus status, Pageable pageable);

    /**
     * Admin listing with optional dynamic filters: exact status and/or a
     * case-insensitive search on the guide title or the game name. Null
     * filters are ignored. Ordered by creation date (newest first).
     */
    @Query("""
            select g from GuideEntity g
            left join fetch g.game
            where (:status is null or g.status = :status)
              and (:search is null
                   or lower(g.title) like lower(concat('%', cast(:search as string), '%'))
                   or lower(g.game.name) like lower(concat('%', cast(:search as string), '%')))
              and (:isTrophyGuide is null 
                   or (:isTrophyGuide = true and g.trophy is not null) 
                   or (:isTrophyGuide = false and g.trophy is null))
            order by g.createdAt desc""")
    Page<GuideEntity> findAllFiltered(@Param("status") GuideStatus status,
                                      @Param("search") String search,
                                      @Param("isTrophyGuide") Boolean isTrophyGuide,
                                      Pageable pageable);

    long countByStatus(GuideStatus status);

    List<GuideEntity> findByTrophyIdAndStatusOrderByUpvotesCountDesc(UUID trophyId, GuideStatus status);

    @Query("select g from GuideEntity g where g.trophy is null and g.game.id = :gameId and g.status = :status order by g.upvotesCount desc")
    List<GuideEntity> findByGameIdAndStatusOrderByUpvotesCountDesc(UUID gameId, GuideStatus status);

    @Query("""
            select g from GuideEntity g
            left join fetch g.game
            where g.trophy is null
              and g.status = :status
              and (:search is null
                   or lower(g.game.name) like lower(concat('%', cast(:search as string), '%')))
            order by g.createdAt desc""")
    List<GuideEntity> findLatestRoadmaps(@Param("status") GuideStatus status,
                                         @Param("search") String search,
                                         Pageable pageable);

    @Query("""
            select g from GuideEntity g
            where g.trophy.game.id = :gameId
              and g.author.id = :authorId
              and g.status = :status
            order by g.trophy.psnTrophyId asc, g.createdAt asc""")
    List<GuideEntity> findTrophyTipsByAuthorAndGame(
            @Param("gameId") UUID gameId,
            @Param("authorId") UUID authorId,
            @Param("status") GuideStatus status);

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