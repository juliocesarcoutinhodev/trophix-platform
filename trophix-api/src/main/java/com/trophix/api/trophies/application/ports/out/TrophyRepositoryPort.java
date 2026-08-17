package com.trophix.api.trophies.application.ports.out;

import com.trophix.api.trophies.model.Trophy;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public interface TrophyRepositoryPort {

    /**
     * Persists only the trophies not yet registered for the game and returns
     * the full catalog (existing + new) with their ids.
     */
    List<Trophy> saveAllIfNotExists(UUID gameId, List<Trophy> trophies);

    List<Trophy> findByGameId(UUID gameId);

    Optional<Trophy> findById(UUID trophyId);

    Optional<Trophy> findByGameIdAndPsnTrophyId(UUID gameId, Integer psnTrophyId);

    /** Batch lookup by id, keyed by trophy id. Missing ids are simply absent. */
    Map<UUID, Trophy> findAllByIds(Collection<UUID> ids);
}