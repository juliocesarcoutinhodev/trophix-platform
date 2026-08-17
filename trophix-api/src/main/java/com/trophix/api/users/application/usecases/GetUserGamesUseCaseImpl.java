package com.trophix.api.users.application.usecases;

import com.trophix.api.games.application.ports.out.UserGameRepositoryPort;
import com.trophix.api.games.model.UserGameSummary;
import com.trophix.api.shared.exception.ResourceNotFoundException;
import com.trophix.api.users.application.ports.in.GetUserGamesUseCase;
import com.trophix.api.users.application.ports.out.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class GetUserGamesUseCaseImpl implements GetUserGamesUseCase {

    private final UserGameRepositoryPort userGameRepository;
    private final UserRepository userRepository;

    @Override
    public Page<UserGameSummary> getGames(String username, Pageable pageable) {
        UUID userId = userRepository.findByUsername(username)
                .map(com.trophix.api.users.model.User::id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado: " + username));
        return userGameRepository.findByUserId(userId, pageable);
    }

    @Override
    public Page<UserGameSummary> getGamesByUserId(UUID userId, Pageable pageable) {
        return userGameRepository.findByUserId(userId, pageable);
    }
}