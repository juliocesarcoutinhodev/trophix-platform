package com.trophix.api.games.infrastructure.adapter.out;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GameSpringDataRepository extends JpaRepository<GameEntity, UUID> {

    Optional<GameEntity> findByNpCommunicationId(String npCommunicationId);

    @Query(value = """
            select g.* from games g
            left join user_games ug on ug.game_id = g.id
            where (:search is null or lower(g.name) like lower('%' || :search || '%'))
            group by g.id, g.np_communication_id, g.name, g.image_url, g.platform, g.total_trophies, g.is_featured
            order by count(ug) desc, g.name asc
            """,
            countQuery = """
            select count(g.id) from games g
            where (:search is null or lower(g.name) like lower('%' || :search || '%'))
            """,
            nativeQuery = true)
    Page<GameEntity> findCatalog(@Param("search") String search, Pageable pageable);

    /** Manually featured games (hybrid trending - source 1). */
    @Query(value = """
            select g.id, g.name, g.image_url,
                   (select count(*) from guides gd where gd.game_id = g.id) as guides_count
            from games g
            where g.is_featured = true
            order by g.id desc
            """, nativeQuery = true)
    List<TrendingRow> findFeaturedGames(Pageable pageable);

    /** Most played games by linked players, newest first on tie (hybrid trending - source 2). */
    @Query(value = """
            select g.id, g.name, g.image_url,
                   (select count(*) from guides gd where gd.game_id = g.id) as guides_count,
                   count(ug.id) as popularity
            from games g
            left join user_games ug on ug.game_id = g.id
            group by g.id, g.name, g.image_url
            order by count(ug.id) desc, g.id desc
            """, nativeQuery = true)
    List<TrendingRow> findPopularGames(Pageable pageable);

    /** Games of the given ids that have no trophies registered yet. */
    @Query(value = """
            select g.id from games g
            left join trophies t on t.game_id = g.id
            where g.id in (:ids)
            group by g.id
            having count(t.id) = 0
            """, nativeQuery = true)
    List<UUID> findGameIdsWithoutTrophies(@Param("ids") Collection<UUID> ids);

    /** Lightweight projection for the hybrid trending list. */
    interface TrendingRow {
        UUID getId();

        String getName();

        String getImageUrl();

        long getGuidesCount();
    }
}