package com.trophix.api.forums.infrastructure.adapter.out;

import com.trophix.api.forums.application.ports.out.CategoryRepository;
import com.trophix.api.forums.model.Category;
import com.trophix.api.forums.model.CategoryListItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class CategoryJpaAdapter implements CategoryRepository {

    private final CategorySpringDataRepository springDataRepository;
    private final CategoryMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public Optional<Category> findById(UUID categoryId) {
        return springDataRepository.findById(categoryId).map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryListItem> findAllWithTopicCounts() {
        return springDataRepository.findAllWithTopicCounts().stream()
                .map(mapper::toListItem)
                .toList();
    }
}
