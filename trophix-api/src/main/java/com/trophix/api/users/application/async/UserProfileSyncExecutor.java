package com.trophix.api.users.application.async;

import com.trophix.api.games.application.ports.out.GameRepositoryPort;
import com.trophix.api.games.application.ports.out.UserGameRepositoryPort;
import com.trophix.api.games.model.Game;
import com.trophix.api.games.model.GameSaveResult;
import com.trophix.api.games.model.UserGame;
import com.trophix.api.shared.application.ports.out.SyncJobPublisher;
import com.trophix.api.shared.exception.ResourceNotFoundException;
import com.trophix.api.users.application.ports.out.PsnSyncPort;
import com.trophix.api.users.application.ports.out.UserRepository;
import com.trophix.api.users.model.PsnProfileSummary;
import com.trophix.api.users.model.PsnUserGame;
import com.trophix.api.users.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
    private final SyncJobPublisher syncJobPublisher;

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
        List<UUID> persistedGameIds = new ArrayList<>();
        Set<UUID> catalogCandidates = new HashSet<>();
        for (PsnUserGame game : games) {
            GameSaveResult result = gameRepository.saveIfNotExists(Game.create(
                    game.npCommunicationId(), game.name(), game.imageUrl(),
                    game.platform(), game.totalTrophies()));
            persistedGameIds.add(result.game().id());
            if (result.created()) {
                catalogCandidates.add(result.game().id());
            }
            userGameRepository.saveOrUpdate(UserGame.create(
                    userId, result.game().id(),
                    game.progress(), game.earnedTrophies(), game.lastPlayedAt()));
        }

        // Jogos já existentes porém sem troféus cadastrados também entram na fila.
        catalogCandidates.addAll(gameRepository.findGameIdsWithoutTrophies(persistedGameIds));

        if (!catalogCandidates.isEmpty()) {
            Set<UUID> toPublish = Set.copyOf(catalogCandidates);
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    toPublish.forEach(syncJobPublisher::publishTrophyCatalogSync);
                }
            });
        }
    }
}
