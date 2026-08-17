package com.trophix.api.news.infrastructure.adapter.out;

import com.trophix.api.news.application.ports.out.NewsArticleRepository;
import com.trophix.api.news.model.NewsArticle;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class NewsArticleJpaAdapter implements NewsArticleRepository {

    private final NewsArticleSpringDataRepository springDataRepository;
    private final NewsArticleMapper mapper;

    @Override
    @Transactional
    public List<NewsArticle> saveIfNotExists(List<NewsArticle> articles) {
        if (articles.isEmpty()) {
            return List.of();
        }
        Set<String> existingLinks = springDataRepository
                .findExistingLinks(articles.stream().map(NewsArticle::link).toList());

        List<NewsArticle> toSave = articles.stream()
                .filter(article -> article.title() != null && !article.title().isBlank())
                .filter(article -> !existingLinks.contains(article.link()))
                .toList();

        if (toSave.isEmpty()) {
            return List.of();
        }
        return springDataRepository.saveAll(toSave.stream().map(mapper::toEntity).toList())
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NewsArticle> findLatest(Pageable pageable) {
        return springDataRepository.findAllByOrderByPublishedAtDesc(pageable).map(mapper::toDomain);
    }

    @Override
    @Transactional
    public void featureNewest() {
        springDataRepository.clearFeatured();
        springDataRepository.findAllByOrderByPublishedAtDesc(PageRequest.of(0, 1))
                .stream()
                .findFirst()
                .ifPresent(entity -> springDataRepository.markFeatured(entity.getId()));
    }
}
