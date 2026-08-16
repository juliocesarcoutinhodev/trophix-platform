package com.trophix.api.forums.application.ports.in;

import com.trophix.api.forums.model.TopicListItem;

import java.util.UUID;

public interface CreateTopicUseCase {

    /** Creates a new topic and returns it enriched with the author info. */
    TopicListItem create(CreateTopicCommand command);

    record CreateTopicCommand(UUID categoryId, UUID authorId, String title, String content) {
    }
}
