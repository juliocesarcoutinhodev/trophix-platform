package com.trophix.api.users.application.usecases;

import com.trophix.api.games.application.ports.out.UserGameRepositoryPort;
import com.trophix.api.games.model.UserGameSummary;
import com.trophix.api.users.application.ports.in.GetUserGamesUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GetUserGamesUseCaseImpl implements GetUserGamesUseCase {

    private final UserGameRepositoryPort userGameRepository;

    @Override
    public Page<UserGameSummary> getGames(String username, Pageable pageable) {
        return userGameRepository.findByUsername(username, pageable);
    }
}