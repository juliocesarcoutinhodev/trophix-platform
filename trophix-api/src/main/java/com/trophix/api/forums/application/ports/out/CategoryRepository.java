package com.trophix.api.forums.application.ports.out;

import com.trophix.api.forums.model.Category;
import com.trophix.api.forums.model.CategoryListItem;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoryRepository {

    Optional<Category> findById(UUID categoryId);

    /** Categories ordered by {@code orderIndex}, each with its topic count. */
    List<CategoryListItem> findAllWithTopicCounts();
}
