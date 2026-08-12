package com.trophix.api.users.application.ports.out;

import com.trophix.api.users.model.PsnProfileSummary;
import com.trophix.api.users.model.PsnUserGame;

import java.util.List;

public interface PsnSyncPort {

    /**
     * Fetches the PSN profile summary for the given online id (psnId),
     * including the numeric accountId.
     */
    PsnProfileSummary fetchProfileSummary(String psnId);

    /**
     * Fetches the list of games played by the account.
     */
    List<PsnUserGame> fetchUserGames(String accountId);
}