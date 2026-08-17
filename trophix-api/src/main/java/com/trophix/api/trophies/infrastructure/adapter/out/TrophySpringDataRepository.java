package com.trophix.api.trophies.infrastructure.adapter.out;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface TrophySpringDataRepository extends JpaRepository<TrophyEntity, UUID> {

    @Query("select t.psnTrophyId from TrophyEntity t where t.gameId = :gameId")
    Set<Integer> findPsnTrophyIdsByGameId(@Param("gameId") UUID gameId);

    List<TrophyEntity> findByGameId(UUID gameId);

    Optional<TrophyEntity> findByGameIdAndPsnTrophyId(UUID gameId, Integer psnTrophyId);

    @Query("select t from TrophyEntity t where t.id in :ids")
    List<TrophyEntity> findAllByIds(@Param("ids") Collection<UUID> ids);

    /**
     * Trophies from the user's games the user has not earned yet, paginated.
     * Ordered by the user's most recently played game, then trophy name.
     */
    @Query(value = """
            select t.id, t.name, t.description, t.type, t.icon_url, t.rarity, g.name as game_name
            from trophies t
            join games g on g.id = t.game_id
            join user_games ug on ug.game_id = t.game_id and ug.user_id = :userId
            where t.id not in (select ut.trophy_id from user_trophies ut where ut.user_id = :userId)
            order by ug.last_played_at desc, t.name asc
            """,
            countQuery = """
            select count(*)
            from trophies t
            join user_games ug on ug.game_id = t.game_id and ug.user_id = :userId
            where t.id not in (select ut.trophy_id from user_trophies ut where ut.user_id = :userId)
            """,
            nativeQuery = true)
    Page<MissingTrophyProjection> findMissingForUser(@Param("userId") UUID userId, Pageable pageable);

    @Modifying
    @Query("update TrophyEntity t set t.rarity = :rarity where t.gameId = :gameId and t.psnTrophyId = :psnTrophyId")
    int updateRarity(@Param("gameId") UUID gameId,
                     @Param("psnTrophyId") Integer psnTrophyId,
                     @Param("rarity") Double rarity);

    interface MissingTrophyProjection {
        UUID getId();

        String getName();

        String getDescription();

        String getType();

        String getIconUrl();

        Double getRarity();

        String getGameName();
    }
}