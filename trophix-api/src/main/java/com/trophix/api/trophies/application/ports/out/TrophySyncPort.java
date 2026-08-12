package com.trophix.api.trophies.application.ports.out;

import com.trophix.api.trophies.model.PsnEarnedTrophy;
import com.trophix.api.trophies.model.PsnTrophy;

import java.util.List;

public interface TrophySyncPort {

    /**
     * Fetches the trophy catalog of a game from the PSN sidecar.
     */
    List<PsnTrophy> fetchGameTrophyCatalog(String npCommunicationId);

    /**
     * Fetches the user's earned status for every trophy of a game.
     */
    List<PsnEarnedTrophy> fetchUserEarnedTrophies(String accountId, String npCommunicationId);
}