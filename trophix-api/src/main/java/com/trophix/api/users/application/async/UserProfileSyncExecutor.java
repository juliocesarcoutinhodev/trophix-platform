package com.trophix.api.users.application.async;

import com.trophix.api.games.application.ports.out.GameRepositoryPort;
import com.trophix.api.games.application.ports.out.UserGameRepositoryPort;
import com.trophix.api.games.model.Game;
import com.trophix.api.games.model.UserGame;
import com.trophix.api.shared.exception.ResourceNotFoundException;
import com.trophix.api.users.application.ports.out.PsnSyncPort;
import com.trophix.api.users.application.ports.out.UserRepository;
import com.trophix.api.users.model.PsnProfileSummary;
import com.trophix.api.users.model.PsnUserGame;
import com.trophix.api.users.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Application service that executes the heavy PSN synchronization. Runs on a
 * worker thread consumed from the async sync queue; exceptions propagate so
 * the queue can retry transient sidecar failures.
 */
@Component
@RequiredArgsConstructor
public class UserProfileSyncExecutor {

    private final UserRepository userRepository;
    private final PsnSyncPort psnSync;
    private final GameRepositoryPort gameRepository;
    private final UserGameRepositoryPort userGameRepository;

    @Transactional
    public void syncProfile(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        PsnProfileSummary summary = psnSync.fetchProfileSummary(user.username());

        String accountId = user.accountId() != null ? user.accountId() : summary.accountId();
        User updatedUser = new User(
                user.id(), user.username(), user.email(), user.password(), user.avatarUrl(),
                user.roles(),
                accountId,
                summary.level(), summary.progress(),
                summary.platinum(), summary.gold(), summary.silver(), summary.bronze(),
                Instant.now());
        userRepository.save(updatedUser);

        List<PsnUserGame> games = psnSync.fetchUserGames(accountId);
        for (PsnUserGame game : games) {
            Game persistedGame = gameRepository.saveIfNotExists(Game.create(
                    game.npCommunicationId(), game.name(), game.imageUrl(),
                    game.platform(), game.totalTrophies()));
            userGameRepository.saveOrUpdate(UserGame.create(
                    userId, persistedGame.id(),
                    game.progress(), game.earnedTrophies(), game.lastPlayedAt()));
        }
    }
}
