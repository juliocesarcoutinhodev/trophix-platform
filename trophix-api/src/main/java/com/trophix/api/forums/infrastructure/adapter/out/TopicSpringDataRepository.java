package com.trophix.api.forums.infrastructure.adapter.out;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface TopicSpringDataRepository extends JpaRepository<TopicJpaEntity, UUID> {

    @Query("select t from TopicJpaEntity t where t.categoryId = :categoryId order by t.updatedAt desc")
    Page<TopicJpaEntity> findByCategoryIdOrderByUpdatedAtDesc(@Param("categoryId") UUID categoryId, Pageable pageable);

    @Modifying
    @Query("update TopicJpaEntity t set t.viewsCount = t.viewsCount + 1 where t.id = :id")
    void incrementViewsCount(@Param("id") UUID id);
}
