package com.trophix.api.users.application.ports.in;

import com.trophix.api.games.model.UserGameSummary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface GetUserGamesUseCase {

    /**
     * Returns the user's games (with game metadata) paginated and ordered
     * by most recently played.
     */
    Page<UserGameSummary> getGames(String username, Pageable pageable);
}