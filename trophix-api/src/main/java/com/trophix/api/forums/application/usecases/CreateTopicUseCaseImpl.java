package com.trophix.api.forums.application.usecases;

import com.trophix.api.forums.application.ports.in.CreateTopicUseCase;
import com.trophix.api.forums.application.ports.out.CategoryRepository;
import com.trophix.api.forums.application.ports.out.TopicRepository;
import com.trophix.api.forums.application.service.ForumEnricher;
import com.trophix.api.forums.model.Topic;
import com.trophix.api.forums.model.TopicListItem;
import com.trophix.api.shared.exception.BusinessException;
import com.trophix.api.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class CreateTopicUseCaseImpl implements CreateTopicUseCase {

    private final CategoryRepository categoryRepository;
    private final TopicRepository topicRepository;
    private final ForumEnricher forumEnricher;

    @Override
    @Transactional
    public TopicListItem create(CreateTopicCommand command) {
        if (command.title() == null || command.title().isBlank()
                || command.content() == null || command.content().isBlank()) {
            throw new BusinessException("Título e conteúdo são obrigatórios.");
        }

        categoryRepository.findById(command.categoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada"));

        Topic topic = Topic.create(command.categoryId(), command.authorId(),
                command.title().trim(), command.content().trim());
        return forumEnricher.enrichTopic(topicRepository.save(topic));
    }
}
