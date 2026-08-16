package com.trophix.api.forums.infrastructure.adapter.out;

import com.trophix.api.forums.infrastructure.adapter.out.CategorySpringDataRepository.CategoryWithTopicCount;
import com.trophix.api.forums.model.Category;
import com.trophix.api.forums.model.CategoryListItem;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {

    public Category toDomain(CategoryJpaEntity entity) {
        return new Category(entity.getId(), entity.getName(), entity.getDescription(), entity.getOrderIndex());
    }

    public CategoryListItem toListItem(CategoryWithTopicCount projection) {
        return new CategoryListItem(projection.getId(), projection.getName(),
                projection.getDescription(), projection.getOrderIndex(), projection.getTopicsCount());
    }
}
