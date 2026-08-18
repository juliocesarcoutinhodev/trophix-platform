package com.trophix.api.games.application.ports.out;

import com.trophix.api.games.model.PsnTrophy;

import java.util.List;
import java.util.UUID;

/**
 * Persists the trophy catalog of an imported game. Owned by the games module
 * and implemented by the trophies module (dependency inversion) to keep the
 * modules free of cycles.
 */
public interface TrophyCatalogPort {

    /** Persists the catalog entries that are not yet registered. */
    int saveCatalog(UUID gameId, List<PsnTrophy> trophies);
}
