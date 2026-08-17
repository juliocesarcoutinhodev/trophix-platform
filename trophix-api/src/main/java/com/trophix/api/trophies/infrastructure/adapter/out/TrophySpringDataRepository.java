package com.trophix.api.trophies.infrastructure.adapter.out;

import org.springframework.data.jpa.repository.JpaRepository;
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
}