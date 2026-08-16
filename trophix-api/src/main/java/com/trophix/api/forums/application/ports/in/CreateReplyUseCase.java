package com.trophix.api.forums.application.ports.in;

import com.trophix.api.forums.model.ReplyListItem;

import java.util.UUID;

public interface CreateReplyUseCase {

    /**
     * Creates a reply, bumps the parent topic (repliesCount + updatedAt) and
     * publishes a local {@code ReplyCreatedEvent}.
     */
    ReplyListItem create(CreateReplyCommand command);

    record CreateReplyCommand(UUID topicId, UUID authorId, String content) {
    }
}
