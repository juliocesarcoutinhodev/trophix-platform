package com.trophix.api.forums.infrastructure.notification;

import com.trophix.api.auth.application.ports.out.EmailSenderPort;
import com.trophix.api.forums.application.ports.out.TopicRepository;
import com.trophix.api.forums.model.ReplyCreatedEvent;
import com.trophix.api.forums.model.Topic;
import com.trophix.api.users.application.ports.out.UserRepository;
import com.trophix.api.users.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

/**
 * Asynchronous, transactional listener for {@link ReplyCreatedEvent}: notifies
 * the topic author by e-mail. Running via Spring Modulith, the listener is
 * invoked in its own transaction after the publishing transaction commits.
 */
@Component
@RequiredArgsConstructor
public class ReplyNotificationListener {

    private final TopicRepository topicRepository;
    private final UserRepository userRepository;
    private final EmailSenderPort emailSender;

    @ApplicationModuleListener
    public void onReplyCreated(ReplyCreatedEvent event) {
        Topic topic = topicRepository.findById(event.topicId()).orElse(null);
        if (topic == null) {
            return;
        }

        User topicAuthor = userRepository.findById(topic.authorId()).orElse(null);
        if (topicAuthor == null || topicAuthor.email() == null || topicAuthor.email().isBlank()) {
            return;
        }
        User replyAuthor = userRepository.findById(event.replyAuthorId()).orElse(null);
        String replyAuthorName = replyAuthor != null ? replyAuthor.username() : "Alguém";

        emailSender.sendReplyNotification(
                topicAuthor.email(),
                topicAuthor.username(),
                replyAuthorName,
                topic.title(),
                topic.id());
    }
}
