package com.trophix.api.forums.application.service;

import com.trophix.api.forums.model.Reply;
import com.trophix.api.forums.model.ReplyListItem;
import com.trophix.api.forums.model.Topic;
import com.trophix.api.forums.model.TopicListItem;
import com.trophix.api.users.application.ports.out.UserRepository;
import com.trophix.api.users.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Assembles forum read models by batch-fetching authors with IN queries,
 * avoiding N+1. Pure application logic.
 */
@Component
@RequiredArgsConstructor
public class ForumEnricher {

    private final UserRepository userRepository;

    public TopicListItem enrichTopic(Topic topic) {
        Map<UUID, User> authorsById = userRepository.findAllByIds(Set.of(topic.authorId()));
        return new TopicListItem(topic,
                usernameOf(authorsById.get(topic.authorId())),
                avatarOf(authorsById.get(topic.authorId())));
    }

    public Page<TopicListItem> enrichTopics(Page<Topic> topics) {
        Map<UUID, User> authorsById = userRepository.findAllByIds(
                topics.getContent().stream().map(Topic::authorId).collect(Collectors.toSet()));
        return topics.map(topic -> new TopicListItem(topic,
                usernameOf(authorsById.get(topic.authorId())),
                avatarOf(authorsById.get(topic.authorId()))));
    }

    public ReplyListItem enrichReply(Reply reply) {
        Map<UUID, User> authorsById = userRepository.findAllByIds(Set.of(reply.authorId()));
        return new ReplyListItem(reply,
                usernameOf(authorsById.get(reply.authorId())),
                avatarOf(authorsById.get(reply.authorId())));
    }

    public Page<ReplyListItem> enrichReplies(Page<Reply> replies) {
        Map<UUID, User> authorsById = userRepository.findAllByIds(
                replies.getContent().stream().map(Reply::authorId).collect(Collectors.toSet()));
        return replies.map(reply -> new ReplyListItem(reply,
                usernameOf(authorsById.get(reply.authorId())),
                avatarOf(authorsById.get(reply.authorId()))));
    }

    private String usernameOf(User user) {
        return user != null ? user.username() : null;
    }

    private String avatarOf(User user) {
        return user != null ? user.avatarUrl() : null;
    }
}
