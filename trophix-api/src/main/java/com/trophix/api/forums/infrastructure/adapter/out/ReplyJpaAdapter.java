package com.trophix.api.forums.infrastructure.adapter.out;

import com.trophix.api.forums.application.ports.out.ReplyRepository;
import com.trophix.api.forums.model.Reply;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ReplyJpaAdapter implements ReplyRepository {

    private final ReplySpringDataRepository springDataRepository;
    private final ReplyMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public Page<Reply> findByTopicId(UUID topicId, Pageable pageable) {
        return springDataRepository.findByTopicIdOrderByCreatedAtAsc(topicId, pageable).map(mapper::toDomain);
    }

    @Override
    @Transactional
    public Reply save(Reply reply) {
        return mapper.toDomain(springDataRepository.save(mapper.toEntity(reply)));
    }
}
