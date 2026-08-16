package com.trophix.api.forums.infrastructure.adapter.in;

import com.trophix.api.forums.application.ports.in.CreateReplyUseCase;
import com.trophix.api.forums.application.ports.in.CreateTopicUseCase;
import com.trophix.api.forums.application.ports.in.GetTopicDetailsUseCase;
import com.trophix.api.forums.application.ports.in.ListCategoriesUseCase;
import com.trophix.api.forums.application.ports.in.ListTopicsByCategoryUseCase;
import com.trophix.api.forums.infrastructure.adapter.in.dto.CategoryResponse;
import com.trophix.api.forums.infrastructure.adapter.in.dto.CreateReplyRequest;
import com.trophix.api.forums.infrastructure.adapter.in.dto.CreateTopicRequest;
import com.trophix.api.forums.infrastructure.adapter.in.dto.ReplyResponse;
import com.trophix.api.forums.infrastructure.adapter.in.dto.TopicDetailsResponse;
import com.trophix.api.forums.infrastructure.adapter.in.dto.TopicResponse;
import com.trophix.api.forums.infrastructure.adapter.in.mapper.ForumWebMapper;
import com.trophix.api.forums.model.ReplyListItem;
import com.trophix.api.forums.model.TopicListItem;
import com.trophix.api.shared.exception.BusinessException;
import com.trophix.api.shared.infrastructure.security.AuthenticatedUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/**
 * Public forum. Reads are open; creating topics/replies requires
 * authentication (enforced at the security layer and via the JWT principal).
 */
@RestController
@RequestMapping("/api/forums")
@RequiredArgsConstructor
public class ForumController {

    private final ListCategoriesUseCase listCategoriesUseCase;
    private final ListTopicsByCategoryUseCase listTopicsByCategoryUseCase;
    private final GetTopicDetailsUseCase getTopicDetailsUseCase;
    private final CreateTopicUseCase createTopicUseCase;
    private final CreateReplyUseCase createReplyUseCase;
    private final ForumWebMapper forumWebMapper;
    private final AuthenticatedUser authenticatedUser;

    @GetMapping("/categories")
    public ResponseEntity<List<CategoryResponse>> listCategories() {
        List<CategoryResponse> categories = listCategoriesUseCase.listCategories().stream()
                .map(forumWebMapper::toCategoryResponse)
                .toList();
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/categories/{categoryId}/topics")
    public ResponseEntity<Page<TopicResponse>> listTopics(
            @PathVariable UUID categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<TopicResponse> topics = listTopicsByCategoryUseCase
                .listTopics(categoryId, PageRequest.of(Math.max(page, 0), Math.min(size, 100)))
                .map(forumWebMapper::toTopicResponse);
        return ResponseEntity.ok(topics);
    }

    @GetMapping("/topics/{topicId}")
    public ResponseEntity<TopicDetailsResponse> getTopicDetails(@PathVariable UUID topicId) {
        GetTopicDetailsUseCase.TopicDetails details =
                getTopicDetailsUseCase.getDetails(topicId, PageRequest.of(0, 20));
        return ResponseEntity.ok(forumWebMapper.toTopicDetailsResponse(details));
    }

    @GetMapping("/topics/{topicId}/replies")
    public ResponseEntity<Page<ReplyResponse>> listReplies(
            @PathVariable UUID topicId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<ReplyResponse> replies = getTopicDetailsUseCase
                .getDetails(topicId, PageRequest.of(Math.max(page, 0), Math.min(size, 100)))
                .replies()
                .map(forumWebMapper::toReplyResponse);
        return ResponseEntity.ok(replies);
    }

    @PostMapping("/topics")
    public ResponseEntity<TopicResponse> createTopic(
            @AuthenticationPrincipal String userId,
            @Valid @RequestBody CreateTopicRequest request) {
        UUID authorId = authenticatedUser.optionalId(userId)
                .orElseThrow(() -> new BusinessException("Autenticação necessária para criar tópicos."));
        TopicListItem created = createTopicUseCase.create(forumWebMapper.toCreateTopicCommand(request, authorId));
        return ResponseEntity.status(HttpStatus.CREATED).body(forumWebMapper.toTopicResponse(created));
    }

    @PostMapping("/topics/{topicId}/replies")
    public ResponseEntity<ReplyResponse> createReply(
            @AuthenticationPrincipal String userId,
            @PathVariable UUID topicId,
            @Valid @RequestBody CreateReplyRequest request) {
        UUID authorId = authenticatedUser.optionalId(userId)
                .orElseThrow(() -> new BusinessException("Autenticação necessária para responder."));
        ReplyListItem created = createReplyUseCase.create(
                forumWebMapper.toCreateReplyCommand(request, topicId, authorId));
        return ResponseEntity.status(HttpStatus.CREATED).body(forumWebMapper.toReplyResponse(created));
    }
}
