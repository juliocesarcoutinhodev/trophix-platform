package com.trophix.api.games.application.ports.in;

import com.trophix.api.games.model.Game;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Public catalog queries over the internal game database.
 */
public interface GetGameCatalogUseCase {

    /** Pageable catalog, optionally filtered by name, ordered by number of owners. */
    Page<Game> getCatalog(String search, Pageable pageable);

    /** Most owned / recently synced games, limited. */
    List<Game> getTrending(int limit);
}
