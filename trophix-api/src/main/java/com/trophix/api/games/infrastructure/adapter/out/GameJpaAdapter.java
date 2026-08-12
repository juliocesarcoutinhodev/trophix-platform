package com.trophix.api.games.infrastructure.adapter.out;

import com.trophix.api.games.application.ports.out.GameRepositoryPort;
import com.trophix.api.games.model.Game;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class GameJpaAdapter implements GameRepositoryPort {

    private final GameSpringDataRepository springDataRepository;
    private final GameMapper mapper;

    @Override
    public Game saveIfNotExists(Game game) {
        return springDataRepository.findByNpCommunicationId(game.npCommunicationId())
                .map(mapper::toDomain)
                .orElseGet(() -> mapper.toDomain(springDataRepository.save(mapper.toEntity(game))));
    }
}