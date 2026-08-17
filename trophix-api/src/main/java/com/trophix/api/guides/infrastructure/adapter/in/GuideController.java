package com.trophix.api.guides.infrastructure.adapter.in;

import com.trophix.api.guides.application.ports.in.GetAuthorTrophyGuidesUseCase;
import com.trophix.api.guides.application.ports.in.GetGameGuidesUseCase;
import com.trophix.api.guides.application.ports.in.GetGuideByIdUseCase;
import com.trophix.api.guides.application.ports.in.GetLatestGuidesUseCase;
import com.trophix.api.guides.application.ports.in.GetTrophyGuidesUseCase;
import com.trophix.api.guides.application.ports.in.ReviewGuideUseCase;
import com.trophix.api.guides.application.ports.in.SubmitGuideUseCase;
import com.trophix.api.guides.application.ports.in.VoteGuideUseCase;
import com.trophix.api.guides.infrastructure.adapter.in.dto.GuideResponse;
import com.trophix.api.shared.dto.MessageResponse;
import com.trophix.api.guides.infrastructure.adapter.in.dto.SubmitGuideRequest;
import com.trophix.api.guides.infrastructure.adapter.in.dto.VoteResponse;
import com.trophix.api.guides.infrastructure.adapter.in.mapper.GuideWebMapper;
import com.trophix.api.guides.model.Guide;
import com.trophix.api.auth.infrastructure.security.AuthenticatedUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class GuideController {

    private final SubmitGuideUseCase submitGuideUseCase;
    private final ReviewGuideUseCase reviewGuideUseCase;
    private final VoteGuideUseCase voteGuideUseCase;
    private final GetTrophyGuidesUseCase getTrophyGuidesUseCase;
    private final GetGameGuidesUseCase getGameGuidesUseCase;
    private final GetLatestGuidesUseCase getLatestGuidesUseCase;
    private final GetGuideByIdUseCase getGuideByIdUseCase;
    private final GetAuthorTrophyGuidesUseCase getAuthorTrophyGuidesUseCase;
    private final GuideWebMapper guideWebMapper;
    private final AuthenticatedUser authenticatedUser;

    @GetMapping("/api/guides")
    public ResponseEntity<List<GuideResponse>> getLatestRoadmaps(
            @AuthenticationPrincipal String userId,
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(required = false) String search) {
        List<GuideResponse> guides = getLatestGuidesUseCase.getLatestRoadmaps(limit, search,
                        authenticatedUser.optionalId(userId).orElse(null)).stream()
                .map(guideWebMapper::toGuideResponse)
                .toList();
        return ResponseEntity.ok(guides);
    }

    @GetMapping("/api/guides/{guideId}")
    public ResponseEntity<GuideResponse> getGuideById(
            @AuthenticationPrincipal String userId,
            @PathVariable UUID guideId) {
        GuideResponse guide = guideWebMapper.toGuideResponse(
                getGuideByIdUseCase.getGuide(guideId, authenticatedUser.optionalId(userId).orElse(null)));
        return ResponseEntity.ok(guide);
    }

    @PostMapping("/api/games/{gameId}/guides")
    public ResponseEntity<MessageResponse> submitGameGuide(
            @AuthenticationPrincipal String userId,
            @PathVariable UUID gameId,
            @Valid @RequestBody SubmitGuideRequest request) {
        String message = submitGuideUseCase.submit(UUID.fromString(userId),
                guideWebMapper.toGameGuideCommand(request, gameId));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(guideWebMapper.toMessageResponse(message));
    }

    @PostMapping("/api/trophies/{trophyId}/guides")
    public ResponseEntity<MessageResponse> submitTrophyGuide(
            @AuthenticationPrincipal String userId,
            @PathVariable UUID trophyId,
            @Valid @RequestBody SubmitGuideRequest request) {
        String message = submitGuideUseCase.submit(UUID.fromString(userId),
                guideWebMapper.toTrophyGuideCommand(request, trophyId));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(guideWebMapper.toMessageResponse(message));
    }

    @PatchMapping("/api/guides/{guideId}/review")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> review(
            @AuthenticationPrincipal String userId,
            @PathVariable UUID guideId,
            @RequestParam("action") ReviewGuideUseCase.ReviewAction action) {
        String message = reviewGuideUseCase.review(UUID.fromString(userId), guideId, action);
        return ResponseEntity.ok(guideWebMapper.toMessageResponse(message));
    }

    @PostMapping("/api/guides/{guideId}/vote")
    public ResponseEntity<VoteResponse> vote(
            @AuthenticationPrincipal String userId,
            @PathVariable UUID guideId) {
        VoteGuideUseCase.VoteResult result = voteGuideUseCase.vote(guideId, UUID.fromString(userId));
        return ResponseEntity.ok(guideWebMapper.toVoteResponse(result));
    }

    @GetMapping("/api/trophies/{trophyId}/guides")
    public ResponseEntity<List<GuideResponse>> getApprovedGuides(@PathVariable UUID trophyId) {
        List<GuideResponse> guides = getTrophyGuidesUseCase.getApprovedGuides(trophyId).stream()
                .map(guideWebMapper::toGuideResponse)
                .toList();
        return ResponseEntity.ok(guides);
    }

    @GetMapping("/api/games/np/{npCommunicationId}/guides")
    public ResponseEntity<List<GuideResponse>> getGameGuides(@PathVariable String npCommunicationId) {
        List<GuideResponse> guides = getGameGuidesUseCase.getApprovedGuides(npCommunicationId).stream()
                .map(guideWebMapper::toGuideResponse)
                .toList();
        return ResponseEntity.ok(guides);
    }

    @GetMapping("/api/games/{gameId}/authors/{authorId}/trophy-guides")
    public ResponseEntity<List<GuideResponse>> getAuthorTrophyGuides(
            @PathVariable UUID gameId,
            @PathVariable UUID authorId) {
        List<GuideResponse> guides = getAuthorTrophyGuidesUseCase.getAuthorTrophyGuides(gameId, authorId).stream()
                .map(guideWebMapper::toGuideResponse)
                .toList();
        return ResponseEntity.ok(guides);
    }
}