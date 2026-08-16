package com.trophix.api.forums.application.ports.out;

import com.trophix.api.forums.model.Reply;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ReplyRepository {

    /** Replies of a topic in reading order (oldest first). */
    Page<Reply> findByTopicId(UUID topicId, Pageable pageable);

    Reply save(Reply reply);
}
