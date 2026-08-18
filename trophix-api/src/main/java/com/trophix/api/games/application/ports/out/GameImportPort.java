package com.trophix.api.games.application.ports.out;

import com.trophix.api.games.model.PsnGameDetail;
import com.trophix.api.games.model.PsnTrophy;

import java.util.List;

/**
 * Driven port that pulls raw PSN data through the sidecar. Owned by the
 * games module and implemented by its outbound adapters.
 */
public interface GameImportPort {

    /** Official metadata (name, cover, platform, trophy count) of a title. */
    PsnGameDetail fetchDetails(String npCommunicationId);

    /** Full catalog of trophies for a title. */
    List<PsnTrophy> fetchTrophyCatalog(String npCommunicationId);
}
