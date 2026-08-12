package com.trophix.api.games.infrastructure.adapter.out;

import com.trophix.api.games.model.UserGameSummary;
import org.springframework.stereotype.Component;

@Component
public class UserGameMapper {

    public UserGameSummary toSummary(UserGameEntity entity) {
        return new UserGameSummary(
                entity.getGame().getId(),
                entity.getGame().getName(),
                entity.getGame().getImageUrl(),
                entity.getGame().getPlatform(),
                entity.getProgressPercentage(),
                entity.getEarnedTrophies(),
                entity.getGame().getTotalTrophies(),
                entity.getLastPlayedAt());
    }
}