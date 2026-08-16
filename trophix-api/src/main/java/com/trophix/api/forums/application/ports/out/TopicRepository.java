package com.trophix.api.forums.application.ports.out;

import com.trophix.api.forums.model.Topic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface TopicRepository {

    Optional<Topic> findById(UUID topicId);

    /** Topics of a category ordered by {@code updatedAt} (newest first). */
    Page<Topic> findByCategoryId(UUID categoryId, Pageable pageable);

    Topic save(Topic topic);

    /** Atomic counter update: avoids lost updates under concurrent views. */
    void incrementViewsCount(UUID topicId);
}
