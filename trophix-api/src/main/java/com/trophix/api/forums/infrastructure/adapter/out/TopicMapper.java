package com.trophix.api.forums.infrastructure.adapter.out;

import com.trophix.api.forums.model.Topic;
import org.springframework.stereotype.Component;

@Component
public class TopicMapper {

    public Topic toDomain(TopicJpaEntity entity) {
        return new Topic(
                entity.getId(),
                entity.getCategoryId(),
                entity.getAuthorId(),
                entity.getTitle(),
                entity.getContent(),
                entity.getViewsCount(),
                entity.getRepliesCount(),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }

    public TopicJpaEntity toEntity(Topic topic) {
        TopicJpaEntity entity = new TopicJpaEntity();
        entity.setId(topic.id());
        entity.setCategoryId(topic.categoryId());
        entity.setAuthorId(topic.authorId());
        entity.setTitle(topic.title());
        entity.setContent(topic.content());
        entity.setViewsCount(topic.viewsCount());
        entity.setRepliesCount(topic.repliesCount());
        entity.setCreatedAt(topic.createdAt());
        entity.setUpdatedAt(topic.updatedAt());
        return entity;
    }
}
