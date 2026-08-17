package com.trophix.api.news.infrastructure.adapter.in.mapper;

import com.trophix.api.news.infrastructure.adapter.in.dto.NewsArticleResponse;
import com.trophix.api.news.model.NewsArticle;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface NewsWebMapper {

    NewsArticleResponse toResponse(NewsArticle article);
}
