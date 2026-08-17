package com.trophix.api.trophies.infrastructure.adapter.out;

import com.trophix.api.trophies.application.ports.out.TrophyRepositoryPort;
import com.trophix.api.trophies.model.Trophy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class TrophyJpaAdapter implements TrophyRepositoryPort {

    private final TrophySpringDataRepository springDataRepository;
    private final TrophyMapper mapper;

    @Override
    @Transactional
    public List<Trophy> saveAllIfNotExists(UUID gameId, List<Trophy> trophies) {
        if (trophies.isEmpty()) {
            return findByGameId(gameId);
        }

        java.util.Set<Integer> existingIds = springDataRepository.findPsnTrophyIdsByGameId(gameId);

        List<TrophyEntity> toSave = trophies.stream()
                .filter(trophy -> !existingIds.contains(trophy.psnTrophyId()))
                .map(trophy -> {
                    TrophyEntity entity = mapper.toEntity(trophy);
                    entity.setGameId(gameId);
                    return entity;
                })
                .toList();

        springDataRepository.saveAll(toSave);

        return findByGameId(gameId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Trophy> findByGameId(UUID gameId) {
        return springDataRepository.findByGameId(gameId).stream().map(mapper::toDomain).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Trophy> findById(UUID trophyId) {
        return springDataRepository.findById(trophyId).map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Trophy> findByGameIdAndPsnTrophyId(UUID gameId, Integer psnTrophyId) {
        return springDataRepository.findByGameIdAndPsnTrophyId(gameId, psnTrophyId).map(mapper::toDomain);
    }
}