package com.trophix.api.guides.infrastructure.adapter.out;

import com.trophix.api.guides.model.Guide;
import org.springframework.stereotype.Component;

@Component
public class GuideMapper {

    public Guide toDomain(GuideEntity entity) {
        return new Guide(
                entity.getId(),
                entity.getTrophy() != null ? entity.getTrophy().getId() : null,
                entity.getGame() != null ? entity.getGame().getId() : null,
                entity.getAuthor().getId(),
                entity.getContent(),
                entity.getVideoUrl(),
                entity.getStatus(),
                entity.getUpvotesCount(),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }
}