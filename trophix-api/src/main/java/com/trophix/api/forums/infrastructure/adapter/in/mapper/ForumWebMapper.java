package com.trophix.api.forums.infrastructure.adapter.in.mapper;

import com.trophix.api.forums.application.ports.in.CreateReplyUseCase;
import com.trophix.api.forums.application.ports.in.CreateTopicUseCase;
import com.trophix.api.forums.application.ports.in.GetTopicDetailsUseCase;
import com.trophix.api.forums.infrastructure.adapter.in.dto.CategoryResponse;
import com.trophix.api.forums.infrastructure.adapter.in.dto.CreateReplyRequest;
import com.trophix.api.forums.infrastructure.adapter.in.dto.CreateTopicRequest;
import com.trophix.api.forums.infrastructure.adapter.in.dto.ReplyResponse;
import com.trophix.api.forums.infrastructure.adapter.in.dto.TopicDetailsResponse;
import com.trophix.api.forums.infrastructure.adapter.in.dto.TopicResponse;
import com.trophix.api.forums.model.CategoryListItem;
import com.trophix.api.forums.model.ReplyListItem;
import com.trophix.api.forums.model.TopicListItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;

import java.util.UUID;

/**
 * Web mapping between HTTP DTOs and the forums application layer.
 * The controller only orchestrates: mapper.toCommand() -> use case -> mapper.toResponse().
 */
@Mapper(componentModel = "spring")
public interface ForumWebMapper {

    CategoryResponse toCategoryResponse(CategoryListItem item);

    @Mapping(source = "topic.id", target = "id")
    @Mapping(source = "topic.categoryId", target = "categoryId")
    @Mapping(source = "topic.authorId", target = "authorId")
    @Mapping(source = "topic.title", target = "title")
    @Mapping(source = "topic.content", target = "content")
    @Mapping(source = "topic.viewsCount", target = "viewsCount")
    @Mapping(source = "topic.repliesCount", target = "repliesCount")
    @Mapping(source = "topic.createdAt", target = "createdAt")
    @Mapping(source = "topic.updatedAt", target = "updatedAt")
    TopicResponse toTopicResponse(TopicListItem item);

    @Mapping(source = "topic.id", target = "id")
    @Mapping(source = "topic.categoryId", target = "categoryId")
    @Mapping(source = "topic.authorId", target = "authorId")
    @Mapping(source = "topic.title", target = "title")
    @Mapping(source = "topic.content", target = "content")
    @Mapping(source = "topic.viewsCount", target = "viewsCount")
    @Mapping(source = "topic.repliesCount", target = "repliesCount")
    @Mapping(source = "topic.createdAt", target = "createdAt")
    @Mapping(source = "topic.updatedAt", target = "updatedAt")
    TopicDetailsResponse toTopicDetailsResponse(GetTopicDetailsUseCase.TopicDetails details);

    default Page<ReplyResponse> toReplyResponses(Page<ReplyListItem> replies) {
        return replies.map(this::toReplyResponse);
    }

    @Mapping(source = "reply.id", target = "id")
    @Mapping(source = "reply.topicId", target = "topicId")
    @Mapping(source = "reply.authorId", target = "authorId")
    @Mapping(source = "reply.content", target = "content")
    @Mapping(source = "reply.createdAt", target = "createdAt")
    ReplyResponse toReplyResponse(ReplyListItem item);

    @Mapping(target = "authorId", source = "authorId")
    CreateTopicUseCase.CreateTopicCommand toCreateTopicCommand(CreateTopicRequest request, UUID authorId);

    @Mapping(target = "authorId", source = "authorId")
    CreateReplyUseCase.CreateReplyCommand toCreateReplyCommand(CreateReplyRequest request, UUID topicId, UUID authorId);
}
