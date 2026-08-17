package com.trophix.api.trophies.application.ports.out;

import com.trophix.api.trophies.model.MissingTrophy;
import com.trophix.api.trophies.model.Trophy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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

    /**
     * Trophies from the user's games the user has not earned yet, paginated.
     * Ordered by most recently played game first, then trophy name.
     */
    Page<MissingTrophy> findMissingForUser(UUID userId, Pageable pageable);

    /** Bulk-updates the rarity of the game's trophies, keyed by PSN trophy id. */
    void updateRarity(UUID gameId, Map<Integer, Double> rarityByPsnTrophyId);
}