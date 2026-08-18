package com.trophix.api.games.infrastructure.adapter.out;

import com.trophix.api.games.application.ports.out.GameRepositoryPort;
import com.trophix.api.games.model.Game;
import com.trophix.api.games.model.GameSaveResult;
import com.trophix.api.games.model.TrendingGame;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class GameJpaAdapter implements GameRepositoryPort {

    private final GameSpringDataRepository springDataRepository;
    private final GameMapper mapper;
    private final EntityManager entityManager;

    @Override
    @Transactional
    public GameSaveResult saveIfNotExists(Game game) {
        Optional<GameEntity> existing = springDataRepository.findByNpCommunicationId(game.npCommunicationId());
        if (existing.isPresent()) {
            return new GameSaveResult(mapper.toDomain(existing.get()), false);
        }
        GameEntity saved = springDataRepository.save(mapper.toEntity(game));
        return new GameSaveResult(mapper.toDomain(saved), true);
    }

    @Override
    @Transactional
    public Game save(Game game) {
        return mapper.toDomain(springDataRepository.saveAndFlush(mapper.toEntity(game)));
    }

    @Override
    @Transactional
    public Game insert(Game game) {
        GameEntity entity = mapper.toEntity(game);
        entityManager.persist(entity);
        entityManager.flush();
        return mapper.toDomain(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Game> findById(UUID gameId) {
        return springDataRepository.findById(gameId).map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Game> findByNpCommunicationId(String npCommunicationId) {
        return springDataRepository.findByNpCommunicationId(npCommunicationId).map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<UUID, Game> findAllByIds(Collection<UUID> ids) {
        if (ids.isEmpty()) {
            return Map.of();
        }
        return springDataRepository.findAllById(ids).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toMap(Game::id, Function.identity()));
    }

    @Override
    @Transactional(readOnly = true)
    public Set<UUID> findGameIdsWithoutTrophies(Collection<UUID> gameIds) {
        if (gameIds.isEmpty()) {
            return Set.of();
        }
        return new HashSet<>(springDataRepository.findGameIdsWithoutTrophies(gameIds));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Game> findCatalog(String search, Pageable pageable) {
        return springDataRepository.findCatalog(search, pageable).map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TrendingGame> findFeatured(int limit) {
        return springDataRepository.findFeaturedGames(PageRequest.of(0, Math.max(1, Math.min(limit, 50)))).stream()
                .map(row -> new TrendingGame(row.getId(), row.getName(), row.getImageUrl(), row.getGuidesCount()))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TrendingGame> findPopular(int limit) {
        return springDataRepository.findPopularGames(PageRequest.of(0, Math.max(1, Math.min(limit, 50)))).stream()
                .map(row -> new TrendingGame(row.getId(), row.getName(), row.getImageUrl(), row.getGuidesCount()))
                .toList();
    }
}
