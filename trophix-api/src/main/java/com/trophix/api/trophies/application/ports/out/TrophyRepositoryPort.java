package com.trophix.api.trophies.application.ports.out;

import com.trophix.api.trophies.model.Trophy;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TrophyRepositoryPort {

    /**
     * Persists only the trophies not yet registered for the game and returns
     * the full catalog (existing + new) with their ids.
     */
    List<Trophy> saveAllIfNotExists(UUID gameId, List<Trophy> trophies);

    List<Trophy> findByGameId(UUID gameId);

    Optional<Trophy> findByGameIdAndPsnTrophyId(UUID gameId, Integer psnTrophyId);
}