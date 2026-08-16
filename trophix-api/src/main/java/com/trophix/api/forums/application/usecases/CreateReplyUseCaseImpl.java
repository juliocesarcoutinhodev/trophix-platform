package com.trophix.api.forums.application.usecases;

import com.trophix.api.forums.application.ports.in.CreateReplyUseCase;
import com.trophix.api.forums.application.ports.out.ReplyRepository;
import com.trophix.api.forums.application.ports.out.TopicRepository;
import com.trophix.api.forums.application.service.ForumEnricher;
import com.trophix.api.forums.model.Reply;
import com.trophix.api.forums.model.ReplyCreatedEvent;
import com.trophix.api.forums.model.ReplyListItem;
import com.trophix.api.forums.model.Topic;
import com.trophix.api.shared.exception.BusinessException;
import com.trophix.api.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class CreateReplyUseCaseImpl implements CreateReplyUseCase {

    private final TopicRepository topicRepository;
    private final ReplyRepository replyRepository;
    private final ForumEnricher forumEnricher;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public ReplyListItem create(CreateReplyCommand command) {
        if (command.content() == null || command.content().isBlank()) {
            throw new BusinessException("A resposta não pode estar vazia.");
        }

        Topic topic = topicRepository.findById(command.topicId())
                .orElseThrow(() -> new ResourceNotFoundException("Tópico não encontrado"));

        Reply reply = Reply.create(topic.id(), command.authorId(), command.content().trim());
        replyRepository.save(reply);

        topicRepository.save(topic.withNewReply());

        eventPublisher.publishEvent(new ReplyCreatedEvent(topic.id(), reply.authorId()));

        return forumEnricher.enrichReply(reply);
    }
}
