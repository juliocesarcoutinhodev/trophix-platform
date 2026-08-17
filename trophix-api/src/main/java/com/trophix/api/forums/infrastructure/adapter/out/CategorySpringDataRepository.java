package com.trophix.api.forums.infrastructure.adapter.out;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface CategorySpringDataRepository extends JpaRepository<CategoryJpaEntity, UUID> {

    @Query(value = """
            select c.id as id, c.name as name, c.description as description, c.order_index as orderIndex,
                   count(t.id) as topicsCount,
                   lt.id as last_topic_id, lt.title as last_topic_title,
                   u.username as last_topic_author, lt.updated_at as last_topic_updated_at
            from forum_categories c
            left join forum_topics t on t.category_id = c.id
            left join forum_topics lt on lt.id = (
                select t2.id from forum_topics t2
                where t2.category_id = c.id
                order by t2.updated_at desc, t2.id desc
                limit 1
            )
            left join users u on u.id = lt.author_id
            group by c.id, c.name, c.description, c.order_index,
                     lt.id, lt.title, lt.author_id, lt.updated_at, u.username
            order by c.order_index asc""",
            nativeQuery = true)
    List<CategoryWithTopicCount> findAllWithTopicCounts();

    interface CategoryWithTopicCount {
        UUID getId();

        String getName();

        String getDescription();

        int getOrderIndex();

        long getTopicsCount();

        UUID getLastTopicId();

        String getLastTopicTitle();

        String getLastTopicAuthor();

        Instant getLastTopicUpdatedAt();
    }
}
