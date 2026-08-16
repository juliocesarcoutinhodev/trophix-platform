package com.trophix.api.forums.infrastructure.adapter.out;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface CategorySpringDataRepository extends JpaRepository<CategoryJpaEntity, UUID> {

    @Query("""
            select c.id as id, c.name as name, c.description as description, c.orderIndex as orderIndex,
                   count(t.id) as topicsCount
            from CategoryJpaEntity c
            left join TopicJpaEntity t on t.categoryId = c.id
            group by c.id, c.name, c.description, c.orderIndex
            order by c.orderIndex asc""")
    List<CategoryWithTopicCount> findAllWithTopicCounts();

    interface CategoryWithTopicCount {
        UUID getId();

        String getName();

        String getDescription();

        int getOrderIndex();

        long getTopicsCount();
    }
}
