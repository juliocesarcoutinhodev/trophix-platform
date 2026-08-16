package com.trophix.api.forums.infrastructure.adapter.out;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface ReplySpringDataRepository extends JpaRepository<ReplyJpaEntity, UUID> {

    @Query("select r from ReplyJpaEntity r where r.topicId = :topicId order by r.createdAt asc")
    Page<ReplyJpaEntity> findByTopicIdOrderByCreatedAtAsc(@Param("topicId") UUID topicId, Pageable pageable);
}
