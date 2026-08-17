package com.trophix.api.guides.infrastructure.adapter.out;

import com.trophix.api.guides.model.GuideStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface GuideSpringDataRepository extends JpaRepository<GuideEntity, UUID> {

    Page<GuideEntity> findByStatus(GuideStatus status, Pageable pageable);

    /**
     * Admin listing with optional dynamic filters: exact status and/or a
     * case-insensitive search on the guide title or the game name. Null
     * filters are ignored. Ordered by creation date (newest first).
     * Native SQL joins the games table by flat FK column (game_id), keeping
     * the guides module free of any JPA dependency on the games module.
     */
    @Query(value = """
            select g.* from guides g
            left join games game on game.id = g.game_id
            where (:status is null or g.status = :status)
              and (:search is null
                   or lower(g.title) like lower('%' || :search || '%')
                   or lower(game.name) like lower('%' || :search || '%'))
              and (:isTrophyGuide is null
                   or (:isTrophyGuide = true and g.trophy_id is not null)
                   or (:isTrophyGuide = false and g.trophy_id is null))
            order by g.created_at desc
            """,
            countQuery = """
            select count(g.id) from guides g
            left join games game on game.id = g.game_id
            where (:status is null or g.status = :status)
              and (:search is null
                   or lower(g.title) like lower('%' || :search || '%')
                   or lower(game.name) like lower('%' || :search || '%'))
              and (:isTrophyGuide is null
                   or (:isTrophyGuide = true and g.trophy_id is not null)
                   or (:isTrophyGuide = false and g.trophy_id is null))
            """,
            nativeQuery = true)
    Page<GuideEntity> findAllFiltered(@Param("status") String status,
                                      @Param("search") String search,
                                      @Param("isTrophyGuide") Boolean isTrophyGuide,
                                      Pageable pageable);

    long countByStatus(GuideStatus status);

    @Query("select count(g) from GuideEntity g where g.status = :status and g.createdAt >= :since")
    long countByStatusSince(@Param("status") GuideStatus status, @Param("since") Instant since);

    List<GuideEntity> findByTrophyIdAndStatusOrderByUpvotesCountDesc(UUID trophyId, GuideStatus status);

    @Query("select g from GuideEntity g where g.trophyId is null and g.gameId = :gameId and g.status = :status order by g.upvotesCount desc")
    List<GuideEntity> findByGameIdAndStatusOrderByUpvotesCountDesc(UUID gameId, GuideStatus status);

    @Query(value = """
            select g.* from guides g
            left join games game on game.id = g.game_id
            where g.trophy_id is null
              and g.status = :status
              and (:search is null
                   or lower(game.name) like lower('%' || :search || '%'))
            order by g.created_at desc
            """, nativeQuery = true)
    List<GuideEntity> findLatestRoadmaps(@Param("status") String status,
                                         @Param("search") String search,
                                         Pageable pageable);

    @Query(value = """
            select g.* from guides g
            join trophies t on t.id = g.trophy_id
            where g.game_id = :gameId
              and g.author_id = :authorId
              and g.status = :status
            order by t.psn_trophy_id asc, g.created_at asc
            """, nativeQuery = true)
    List<GuideEntity> findTrophyTipsByAuthorAndGame(
            @Param("gameId") UUID gameId,
            @Param("authorId") UUID authorId,
            @Param("status") String status);

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