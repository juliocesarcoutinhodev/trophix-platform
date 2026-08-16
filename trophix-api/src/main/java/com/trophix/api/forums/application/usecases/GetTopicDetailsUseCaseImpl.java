package com.trophix.api.forums.application.usecases;

import com.trophix.api.forums.application.ports.in.GetTopicDetailsUseCase;
import com.trophix.api.forums.application.ports.out.ReplyRepository;
import com.trophix.api.forums.application.ports.out.TopicRepository;
import com.trophix.api.forums.application.service.ForumEnricher;
import com.trophix.api.forums.application.service.TopicViewCounter;
import com.trophix.api.forums.model.ReplyListItem;
import com.trophix.api.forums.model.Topic;
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
public class GetTopicDetailsUseCaseImpl implements GetTopicDetailsUseCase {

    private final TopicRepository topicRepository;
    private final ReplyRepository replyRepository;
    private final ForumEnricher forumEnricher;
    private final TopicViewCounter topicViewCounter;

    @Override
    @Transactional(readOnly = true)
    public TopicDetails getDetails(UUID topicId, Pageable repliesPageable) {
        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new ResourceNotFoundException("Tópico não encontrado"));

        // Incremento assíncrono: não bloqueia a resposta nem a leitura.
        topicViewCounter.incrementViews(topicId);

        TopicListItem topicItem = forumEnricher.enrichTopic(topic);
        Page<ReplyListItem> replies = forumEnricher.enrichReplies(
                replyRepository.findByTopicId(topicId, repliesPageable));

        return new TopicDetails(topicItem.topic(), topicItem.authorName(), topicItem.authorAvatarUrl(), replies);
    }
}
