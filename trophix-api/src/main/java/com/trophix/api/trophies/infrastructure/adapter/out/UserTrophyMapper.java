package com.trophix.api.trophies.infrastructure.adapter.out;

import com.trophix.api.trophies.model.UserTrophy;
import org.springframework.stereotype.Component;

@Component
class UserTrophyMapper {

    UserTrophy toDomain(UserTrophyEntity entity) {
        return new UserTrophy(
                entity.getId(),
                entity.getUserId(),
                entity.getTrophy().getId(),
                entity.getEarnedAt());
    }
}
