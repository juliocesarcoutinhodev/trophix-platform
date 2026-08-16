package com.trophix.api.forums.application.ports.in;

import com.trophix.api.forums.model.CategoryListItem;

import java.util.List;

public interface ListCategoriesUseCase {

    /** All forum categories ordered by {@code orderIndex}, with topic counts. */
    List<CategoryListItem> listCategories();
}
