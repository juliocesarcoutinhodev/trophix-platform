package com.trophix.api.forums.application.ports.in;

import com.trophix.api.forums.model.TopicListItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ListTopicsByCategoryUseCase {

    /** Paginated topics of a category, newest activity first, with author info. */
    Page<TopicListItem> listTopics(UUID categoryId, Pageable pageable);
}
