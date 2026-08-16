package com.trophix.api.forums.application.usecases;

import com.trophix.api.forums.application.ports.in.ListTopicsByCategoryUseCase;
import com.trophix.api.forums.application.ports.out.CategoryRepository;
import com.trophix.api.forums.application.ports.out.TopicRepository;
import com.trophix.api.forums.application.service.ForumEnricher;
import com.trophix.api.forums.model.TopicListItem;
import com.trophix.api.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ListTopicsByCategoryUseCaseImpl implements ListTopicsByCategoryUseCase {

    private final CategoryRepository categoryRepository;
    private final TopicRepository topicRepository;
    private final ForumEnricher forumEnricher;

    @Override
    @Transactional(readOnly = true)
    public Page<TopicListItem> listTopics(UUID categoryId, Pageable pageable) {
        categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada"));
        return forumEnricher.enrichTopics(topicRepository.findByCategoryId(categoryId, pageable));
    }
}
