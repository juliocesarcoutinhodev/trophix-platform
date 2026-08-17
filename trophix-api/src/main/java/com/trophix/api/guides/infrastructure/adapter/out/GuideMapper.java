package com.trophix.api.guides.infrastructure.adapter.out;

import com.trophix.api.guides.model.Guide;
import org.springframework.stereotype.Component;

@Component
public class GuideMapper {

    public Guide toDomain(GuideEntity entity) {
        return new Guide(
                entity.getId(),
                entity.getTrophyId(),
                entity.getGameId(),
                entity.getAuthorId(),
                entity.getTitle(),
                entity.getDescription(),
                entity.getContent(),
                entity.getVideoUrl(),
                entity.getStatus(),
                entity.getUpvotesCount(),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }
}