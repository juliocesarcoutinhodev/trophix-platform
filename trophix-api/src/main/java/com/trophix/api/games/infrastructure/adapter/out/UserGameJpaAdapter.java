package com.trophix.api.games.infrastructure.adapter.out;

import com.trophix.api.games.application.ports.out.UserGameRepositoryPort;
import com.trophix.api.games.model.UserGame;
import com.trophix.api.games.model.UserGameSummary;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class UserGameJpaAdapter implements UserGameRepositoryPort {

    private final UserGameSpringDataRepository springDataRepository;
    private final GameSpringDataRepository gameSpringDataRepository;
    private final UserGameMapper mapper;

    @Override
    @Transactional
    public void saveOrUpdate(UserGame userGame) {
        UserGameEntity entity = springDataRepository
                .findByUserIdAndGameId(userGame.userId(), userGame.gameId())
                .orElseGet(() -> {
                    UserGameEntity created = new UserGameEntity();
                    created.setUserId(userGame.userId());
                    created.setGame(gameSpringDataRepository.getReferenceById(userGame.gameId()));
                    return created;
                });

        entity.setProgressPercentage(userGame.progressPercentage());
        entity.setEarnedTrophies(userGame.earnedTrophies());
        entity.setLastPlayedAt(userGame.lastPlayedAt());

        springDataRepository.save(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserGameSummary> findByUserIdAndGameId(UUID userId, UUID gameId) {
        return springDataRepository.findByUserIdAndGameId(userId, gameId).map(mapper::toSummary);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserGameSummary> findByUserId(UUID userId, Pageable pageable) {
        return springDataRepository
                .findByUserIdOrderByLastPlayedAtDesc(userId, pageable)
                .map(mapper::toSummary);
    }
}