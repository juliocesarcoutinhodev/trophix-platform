package com.trophix.api.games.infrastructure.adapter.in.mapper;

import com.trophix.api.games.infrastructure.adapter.in.dto.GameCatalogDTO;
import com.trophix.api.games.infrastructure.adapter.in.dto.GameDetailResponse;
import com.trophix.api.games.infrastructure.adapter.in.dto.TrendingGameResponse;
import com.trophix.api.games.model.Game;
import com.trophix.api.games.model.GameDetail;
import com.trophix.api.games.model.TrendingGame;
import org.springframework.stereotype.Component;

@Component
public class GameWebMapper {

    public GameDetailResponse toGameDetailResponse(GameDetail detail) {
        return new GameDetailResponse(
                detail.gameId(),
                detail.name(),
                detail.imageUrl(),
                detail.platform(),
                detail.progressPercentage(),
                detail.earnedTrophies(),
                detail.totalTrophies(),
                new GameDetailResponse.RarityResponse(
                        detail.rarity().platinum(),
                        detail.rarity().gold(),
                        detail.rarity().silver(),
                        detail.rarity().bronze()));
    }

    public GameCatalogDTO toGameCatalogDTO(Game game) {
        return new GameCatalogDTO(game.id(), game.name(), game.imageUrl(), game.featured());
    }

    public TrendingGameResponse toTrendingGameResponse(TrendingGame game) {
        return new TrendingGameResponse(game.id(), game.name(), game.imageUrl(), game.guidesCount());
    }
}
