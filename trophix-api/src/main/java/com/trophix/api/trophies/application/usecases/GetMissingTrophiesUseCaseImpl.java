package com.trophix.api.trophies.application.usecases;

import com.trophix.api.trophies.application.ports.in.GetMissingTrophiesUseCase;
import com.trophix.api.trophies.application.ports.out.TrophyRepositoryPort;
import com.trophix.api.trophies.application.ports.out.UserTrophyRepositoryPort;
import com.trophix.api.trophies.model.Trophy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class GetMissingTrophiesUseCaseImpl implements GetMissingTrophiesUseCase {

    private final TrophyRepositoryPort trophyRepository;
    private final UserTrophyRepositoryPort userTrophyRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Trophy> getMissingTrophies(UUID userId, UUID gameId) {
        List<Trophy> allTrophies = trophyRepository.findByGameId(gameId);
        if (allTrophies.isEmpty()) {
            return List.of();
        }
        Map<UUID, Instant> earnedAtByTrophy = userTrophyRepository.findEarnedAtByUserIdAndTrophyIds(
                userId, allTrophies.stream().map(Trophy::id).toList());
        return allTrophies.stream()
                .filter(trophy -> !earnedAtByTrophy.containsKey(trophy.id()))
                .toList();
    }
}
