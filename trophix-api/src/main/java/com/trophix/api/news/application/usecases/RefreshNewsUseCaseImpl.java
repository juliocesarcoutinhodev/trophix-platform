package com.trophix.api.news.application.usecases;

import com.trophix.api.news.application.ports.in.RefreshNewsUseCase;
import com.trophix.api.news.application.ports.out.NewsArticleRepository;
import com.trophix.api.news.application.ports.out.NewsFeedFetcher;
import com.trophix.api.news.model.NewsArticle;
import com.trophix.api.news.model.NewsDiscoveredEvent;
import com.trophix.api.news.model.NewsFeedItem;
import com.trophix.api.news.model.NewsSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Polls every configured source, persists new articles (dedup by link) and
 * publishes a {@link NewsDiscoveredEvent} for each newly saved one. A failing
 * feed never breaks the remaining sources.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RefreshNewsUseCaseImpl implements RefreshNewsUseCase {

    private final NewsFeedFetcher feedFetcher;
    private final NewsArticleRepository newsArticleRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public int refresh(List<NewsSource> sources) {
        int newCount = 0;
        for (NewsSource source : sources) {
            newCount += refreshSource(source);
        }
        if (newCount > 0) {
            newsArticleRepository.featureNewest();
        }
        return newCount;
    }

    private int refreshSource(NewsSource source) {
        try {
            List<NewsFeedItem> items = feedFetcher.fetch(source);
            List<NewsArticle> candidates = items.stream()
                    .map(item -> NewsArticle.create(
                            item.title(), item.link(), item.imageUrl(), source.name(), item.publishedAt()))
                    .toList();

            List<NewsArticle> saved = newsArticleRepository.saveIfNotExists(candidates);
            saved.forEach(article -> eventPublisher.publishEvent(new NewsDiscoveredEvent(article)));
            log.info("Feed {}: {} itens, {} novos.", source.name(), items.size(), saved.size());
            return saved.size();
        } catch (Exception ex) {
            log.warn("Falha ao buscar feed '{}': {}", source.name(), ex.getMessage());
            return 0;
        }
    }
}
