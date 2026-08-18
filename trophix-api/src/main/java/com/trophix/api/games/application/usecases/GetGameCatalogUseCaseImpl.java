package com.trophix.api.games.application.usecases;

import com.trophix.api.games.application.ports.in.GetGameCatalogUseCase;
import com.trophix.api.games.application.ports.out.GameRepositoryPort;
import com.trophix.api.games.model.Game;
import com.trophix.api.games.model.TrendingGame;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class GetGameCatalogUseCaseImpl implements GetGameCatalogUseCase {

    private final GameRepositoryPort gameRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<Game> getCatalog(String search, Pageable pageable) {
        return gameRepository.findCatalog(search, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TrendingGame> getTrending(int limit) {
        int safeLimit = Math.max(1, Math.min(limit, 5));
        List<TrendingGame> result = new ArrayList<>(gameRepository.findFeatured(safeLimit));
        if (result.size() >= safeLimit) {
            return result;
        }
        Set<UUID> taken = result.stream().map(TrendingGame::id).collect(Collectors.toSet());
        for (TrendingGame game : gameRepository.findPopular(safeLimit)) {
            if (taken.contains(game.id())) {
                continue;
            }
            result.add(game);
            if (result.size() == safeLimit) {
                break;
            }
        }
        return result;
    }
}
