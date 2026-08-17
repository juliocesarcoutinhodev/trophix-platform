package com.trophix.api.games.infrastructure.adapter.out;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface GameSpringDataRepository extends JpaRepository<GameEntity, UUID> {

    Optional<GameEntity> findByNpCommunicationId(String npCommunicationId);

    @Query(value = """
            select g.* from games g
            left join user_games ug on ug.game_id = g.id
            where (:search is null or lower(g.name) like lower('%' || :search || '%'))
            group by g.id, g.np_communication_id, g.name, g.image_url, g.platform, g.total_trophies
            order by count(ug) desc, g.name asc
            """,
            countQuery = """
            select count(g.id) from games g
            where (:search is null or lower(g.name) like lower('%' || :search || '%'))
            """,
            nativeQuery = true)
    Page<GameEntity> findCatalog(@Param("search") String search, Pageable pageable);

    @Query(value = """
            select g.* from games g
            left join user_games ug on ug.game_id = g.id
            group by g.id, g.np_communication_id, g.name, g.image_url, g.platform, g.total_trophies
            order by count(ug) desc, g.name asc
            """, nativeQuery = true)
    java.util.List<GameEntity> findTrending(Pageable pageable);
}