package com.trophix.api.news.application.ports.out;

import com.trophix.api.news.model.NewsFeedItem;
import com.trophix.api.news.model.NewsSource;

import java.util.List;

/**
 * Driven port: fetches and parses a feed into raw {@link NewsFeedItem}s.
 * Implemented with ROME + blocking RestClient (virtual threads handle the I/O).
 */
public interface NewsFeedFetcher {

    List<NewsFeedItem> fetch(NewsSource source);
}
