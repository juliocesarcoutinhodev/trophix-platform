package com.trophix.api.trophies.infrastructure.adapter.out;

import com.trophix.api.trophies.model.Trophy;
import org.springframework.stereotype.Component;

@Component
public class TrophyMapper {

    public Trophy toDomain(TrophyEntity entity) {
        return new Trophy(
                entity.getId(),
                entity.getGameId(),
                entity.getPsnTrophyId(),
                entity.getName(),
                entity.getDescription(),
                entity.getType(),
                entity.getIconUrl(),
                entity.getRarity());
    }

    public TrophyEntity toEntity(Trophy trophy) {
        TrophyEntity entity = new TrophyEntity();
        entity.setId(trophy.id());
        entity.setPsnTrophyId(trophy.psnTrophyId());
        entity.setName(trophy.name());
        entity.setDescription(trophy.description());
        entity.setType(trophy.type());
        entity.setIconUrl(trophy.iconUrl());
        entity.setRarity(trophy.rarity());
        return entity;
    }
}