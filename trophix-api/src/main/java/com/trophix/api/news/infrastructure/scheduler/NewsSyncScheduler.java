package com.trophix.api.news.infrastructure.scheduler;

import com.trophix.api.news.application.ports.in.RefreshNewsUseCase;
import com.trophix.api.news.infrastructure.config.NewsProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Driving adapter: polls the configured RSS feeds periodically. Blocking I/O is
 * fine — scheduling runs on its own thread and the app uses virtual threads.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class NewsSyncScheduler {

    private final RefreshNewsUseCase refreshNewsUseCase;
    private final NewsProperties newsProperties;

    @Scheduled(
            fixedDelayString = "${trophix.news.refresh-interval-ms:1800000}",
            initialDelayString = "${trophix.news.initial-delay-ms:20000}")
    public void refreshNews() {
        try {
            int newCount = refreshNewsUseCase.refresh(newsProperties.sources());
            log.info("Sincronização de notícias concluída: {} novas publicadas.", newCount);
        } catch (Exception ex) {
            log.error("Falha na sincronização de notícias: {}", ex.getMessage());
        }
    }
}
