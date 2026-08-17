package com.trophix.api.trophies.infrastructure.adapter.out;

import com.trophix.api.trophies.application.ports.out.UserTrophyRepositoryPort;
import com.trophix.api.trophies.model.UserTrophy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class UserTrophyJpaAdapter implements UserTrophyRepositoryPort {

    private final UserTrophySpringDataRepository springDataRepository;
    private final TrophySpringDataRepository trophySpringDataRepository;

    @Override
    @Transactional
    public void saveAll(List<UserTrophy> userTrophies) {
        for (UserTrophy userTrophy : userTrophies) {
            UserTrophyEntity entity = springDataRepository
                    .findByUserIdAndTrophyId(userTrophy.userId(), userTrophy.trophyId())
                    .orElseGet(() -> {
                        UserTrophyEntity created = new UserTrophyEntity();
                        created.setUserId(userTrophy.userId());
                        created.setTrophy(trophySpringDataRepository.getReferenceById(userTrophy.trophyId()));
                        return created;
                    });

            entity.setEarnedAt(userTrophy.earnedAt());
            springDataRepository.save(entity);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Map<UUID, Instant> findEarnedAtByUserIdAndTrophyIds(UUID userId, List<UUID> trophyIds) {
        if (trophyIds.isEmpty()) {
            return Map.of();
        }
        return springDataRepository.findByUserIdAndTrophyIdIn(userId, trophyIds).stream()
                .collect(Collectors.toMap(
                        entity -> entity.getTrophy().getId(),
                        UserTrophyEntity::getEarnedAt));
    }
}