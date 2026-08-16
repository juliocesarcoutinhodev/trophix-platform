package com.trophix.api.forums.application.service;

import com.trophix.api.forums.application.ports.out.TopicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Increments a topic's view counter on a separate thread so reading a topic
 * never blocks the HTTP response.
 */
@Component
@RequiredArgsConstructor
public class TopicViewCounter {

    private final TopicRepository topicRepository;

    @Async
    @Transactional
    public void incrementViews(UUID topicId) {
        topicRepository.incrementViewsCount(topicId);
    }
}
