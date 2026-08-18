package com.trophix.api.games.application.ports.in;

import com.trophix.api.games.model.Game;
import com.trophix.api.games.model.TrendingGame;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Public catalog queries over the internal game database.
 */
public interface GetGameCatalogUseCase {

    /** Pageable catalog, optionally filtered by name, ordered by number of owners. */
    Page<Game> getCatalog(String search, Pageable pageable);

    /**
     * Hybrid trending: up to {@code limit} games, starting with the manually
     * featured ones and completing the list with the most played games.
     */
    List<TrendingGame> getTrending(int limit);
}
