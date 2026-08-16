package com.trophix.api.forums.application.usecases;

import com.trophix.api.forums.application.ports.in.ListCategoriesUseCase;
import com.trophix.api.forums.application.ports.out.CategoryRepository;
import com.trophix.api.forums.model.CategoryListItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ListCategoriesUseCaseImpl implements ListCategoriesUseCase {

    private final CategoryRepository categoryRepository;

    @Override
    @Transactional(readOnly = true)
    public List<CategoryListItem> listCategories() {
        return categoryRepository.findAllWithTopicCounts();
    }
}
