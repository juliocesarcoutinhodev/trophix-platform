package com.trophix.api.forums.infrastructure.adapter.out;

import com.trophix.api.forums.model.Reply;
import org.springframework.stereotype.Component;

@Component
public class ReplyMapper {

    public Reply toDomain(ReplyJpaEntity entity) {
        return new Reply(
                entity.getId(),
                entity.getTopicId(),
                entity.getAuthorId(),
                entity.getContent(),
                entity.getCreatedAt());
    }

    public ReplyJpaEntity toEntity(Reply reply) {
        ReplyJpaEntity entity = new ReplyJpaEntity();
        entity.setId(reply.id());
        entity.setTopicId(reply.topicId());
        entity.setAuthorId(reply.authorId());
        entity.setContent(reply.content());
        entity.setCreatedAt(reply.createdAt());
        return entity;
    }
}
