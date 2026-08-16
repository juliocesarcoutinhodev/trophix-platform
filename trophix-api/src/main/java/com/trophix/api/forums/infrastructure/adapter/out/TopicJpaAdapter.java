package com.trophix.api.forums.infrastructure.adapter.out;

import com.trophix.api.forums.application.ports.out.TopicRepository;
import com.trophix.api.forums.model.Topic;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class TopicJpaAdapter implements TopicRepository {

    private final TopicSpringDataRepository springDataRepository;
    private final TopicMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public Optional<Topic> findById(UUID topicId) {
        return springDataRepository.findById(topicId).map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Topic> findByCategoryId(UUID categoryId, Pageable pageable) {
        return springDataRepository.findByCategoryIdOrderByUpdatedAtDesc(categoryId, pageable).map(mapper::toDomain);
    }

    @Override
    @Transactional
    public Topic save(Topic topic) {
        return mapper.toDomain(springDataRepository.save(mapper.toEntity(topic)));
    }

    @Override
    @Transactional
    public void incrementViewsCount(UUID topicId) {
        springDataRepository.incrementViewsCount(topicId);
    }
}
