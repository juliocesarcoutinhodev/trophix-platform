package com.trophix.api.forums.application.ports.in;

import com.trophix.api.forums.model.ReplyListItem;
import com.trophix.api.forums.model.Topic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface GetTopicDetailsUseCase {

    /** Topic with its author and a page of replies (oldest first). */
    TopicDetails getDetails(UUID topicId, Pageable repliesPageable);

    record TopicDetails(
            Topic topic,
            String authorName,
            String authorAvatarUrl,
            Page<ReplyListItem> replies) {
    }
}
