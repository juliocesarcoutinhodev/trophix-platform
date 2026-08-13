package com.trophix.api.trophies.application.usecases;

import com.trophix.api.games.application.ports.out.GameRepositoryPort;
import com.trophix.api.shared.exception.ResourceNotFoundException;
import com.trophix.api.trophies.application.ports.in.GetMyTrophiesUseCase;
import com.trophix.api.trophies.application.ports.out.TrophyRepositoryPort;
import com.trophix.api.trophies.application.ports.out.UserTrophyRepositoryPort;
import com.trophix.api.trophies.model.Trophy;
import com.trophix.api.trophies.model.TrophyWithStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class GetMyTrophiesUseCaseImpl implements GetMyTrophiesUseCase {

    private final GameRepositoryPort gameRepository;
    private final TrophyRepositoryPort trophyRepository;
    private final UserTrophyRepositoryPort userTrophyRepository;

    @Override
    public List<TrophyWithStatus> getMyTrophies(UUID userId, UUID gameId) {
        gameRepository.findById(gameId)
                .orElseThrow(() -> new ResourceNotFoundException("Jogo não encontrado"));

        List<Trophy> trophies = trophyRepository.findByGameId(gameId);
        Map<UUID, Instant> earnedAtByTrophy = userTrophyRepository
                .findEarnedAtByUserIdAndTrophyIds(userId, trophies.stream().map(Trophy::id).toList());

        return trophies.stream()
                .map(trophy -> {
                    Instant earnedAt = earnedAtByTrophy.get(trophy.id());
                    return earnedAt != null
                            ? TrophyWithStatus.earned(trophy, earnedAt)
                            : TrophyWithStatus.locked(trophy);
                })
                .toList();
    }
}
