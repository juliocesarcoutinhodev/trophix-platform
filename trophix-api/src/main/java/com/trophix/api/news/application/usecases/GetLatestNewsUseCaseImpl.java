package com.trophix.api.news.application.usecases;

import com.trophix.api.news.application.ports.in.GetLatestNewsUseCase;
import com.trophix.api.news.application.ports.out.NewsArticleRepository;
import com.trophix.api.news.model.NewsArticle;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class GetLatestNewsUseCaseImpl implements GetLatestNewsUseCase {

    private final NewsArticleRepository newsArticleRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<NewsArticle> getLatest(Pageable pageable) {
        return newsArticleRepository.findLatest(pageable);
    }
}
