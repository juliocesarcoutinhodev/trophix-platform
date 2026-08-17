package com.trophix.api.trophies.application.usecases;

import com.trophix.api.games.application.ports.in.GetGameDetailUseCase;
import com.trophix.api.games.application.ports.out.UserGameRepositoryPort;
import com.trophix.api.games.model.GameDetail;
import com.trophix.api.games.model.UserGameSummary;
import com.trophix.api.shared.exception.ResourceNotFoundException;
import com.trophix.api.trophies.application.ports.out.TrophyRepositoryPort;
import com.trophix.api.trophies.application.ports.out.UserTrophyRepositoryPort;
import com.trophix.api.trophies.model.Trophy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class GetGameDetailUseCaseImpl implements GetGameDetailUseCase {

    private static final String TYPE_PLATINUM = "Platinum";
    private static final String TYPE_GOLD = "Gold";
    private static final String TYPE_SILVER = "Silver";
    private static final String TYPE_BRONZE = "Bronze";

    private final UserGameRepositoryPort userGameRepository;
    private final TrophyRepositoryPort trophyRepository;
    private final UserTrophyRepositoryPort userTrophyRepository;

    @Override
    public GameDetail getGameDetail(UUID userId, UUID gameId) {
        UserGameSummary summary = userGameRepository.findByUserIdAndGameId(userId, gameId)
                .orElseThrow(() -> new ResourceNotFoundException("Jogo não encontrado no seu catálogo"));

        List<Trophy> trophies = trophyRepository.findByGameId(gameId);
        Map<UUID, Instant> earnedAtByTrophy = userTrophyRepository
                .findEarnedAtByUserIdAndTrophyIds(userId, trophies.stream().map(Trophy::id).toList());

        GameDetail.RarityBreakdown rarity = new GameDetail.RarityBreakdown(
                countByType(earnedAtByTrophy, trophies, TYPE_PLATINUM),
                countByType(earnedAtByTrophy, trophies, TYPE_GOLD),
                countByType(earnedAtByTrophy, trophies, TYPE_SILVER),
                countByType(earnedAtByTrophy, trophies, TYPE_BRONZE));

        return new GameDetail(
                summary.gameId(),
                summary.name(),
                summary.imageUrl(),
                summary.platform(),
                summary.progressPercentage(),
                summary.earnedTrophies(),
                summary.totalTrophies(),
                rarity);
    }

    private int countByType(Map<UUID, Instant> earnedAtByTrophy, List<Trophy> trophies, String type) {
        return (int) trophies.stream()
                .filter(trophy -> type.equals(trophy.type()))
                .filter(trophy -> earnedAtByTrophy.containsKey(trophy.id()))
                .count();
    }
}
