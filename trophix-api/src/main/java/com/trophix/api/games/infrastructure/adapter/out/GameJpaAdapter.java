package com.trophix.api.games.infrastructure.adapter.out;

import com.trophix.api.games.application.ports.out.GameRepositoryPort;
import com.trophix.api.games.model.Game;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class GameJpaAdapter implements GameRepositoryPort {

    private final GameSpringDataRepository springDataRepository;
    private final GameMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public Game saveIfNotExists(Game game) {
        return springDataRepository.findByNpCommunicationId(game.npCommunicationId())
                .map(mapper::toDomain)
                .orElseGet(() -> mapper.toDomain(springDataRepository.save(mapper.toEntity(game))));
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
    public Page<Game> findCatalog(String search, Pageable pageable) {
        return springDataRepository.findCatalog(search, pageable).map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Game> findTrending(int limit) {
        int safeLimit = Math.max(1, Math.min(limit, 50));
        return springDataRepository.findTrending(PageRequest.of(0, safeLimit)).stream()
                .map(mapper::toDomain)
                .toList();
    }
}
