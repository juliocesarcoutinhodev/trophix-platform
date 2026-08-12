package com.trophix.api.games.infrastructure.adapter.out;

import com.trophix.api.games.model.Game;
import org.springframework.stereotype.Component;

@Component
public class GameMapper {

    public Game toDomain(GameEntity entity) {
        return new Game(
                entity.getId(),
                entity.getNpCommunicationId(),
                entity.getName(),
                entity.getImageUrl(),
                entity.getPlatform(),
                entity.getTotalTrophies());
    }

    public GameEntity toEntity(Game game) {
        GameEntity entity = new GameEntity();
        entity.setId(game.id());
        entity.setNpCommunicationId(game.npCommunicationId());
        entity.setName(game.name());
        entity.setImageUrl(game.imageUrl());
        entity.setPlatform(game.platform());
        entity.setTotalTrophies(game.totalTrophies());
        return entity;
    }
}