package com.trophix.api.admin.infrastructure.adapter.in;

import com.trophix.api.admin.application.ports.in.DeleteGuideUseCase;
import com.trophix.api.admin.application.ports.in.GetDashboardStatsUseCase;
import com.trophix.api.admin.application.ports.in.GetPendingGuidesUseCase;
import com.trophix.api.admin.application.ports.in.GetSidecarStatusUseCase;
import com.trophix.api.admin.application.ports.in.GetSystemHealthUseCase;
import com.trophix.api.admin.application.ports.in.ListAdminUsersUseCase;
import com.trophix.api.admin.application.ports.in.ListAllGuidesUseCase;
import com.trophix.api.admin.application.ports.in.SetGameFeaturedUseCase;
import com.trophix.api.admin.application.ports.in.UpdateGuideUseCase;
import com.trophix.api.admin.application.ports.in.UpdateUserRolesUseCase;
import com.trophix.api.admin.infrastructure.adapter.in.dto.AdminDashboardStatsResponse;
import com.trophix.api.admin.infrastructure.adapter.in.dto.AdminUserResponse;
import com.trophix.api.admin.infrastructure.adapter.in.dto.ModerationGuideResponse;
import com.trophix.api.admin.infrastructure.adapter.in.dto.SidecarStatusResponse;
import com.trophix.api.admin.infrastructure.adapter.in.dto.SystemHealthResponse;
import com.trophix.api.admin.infrastructure.adapter.in.dto.UpdateGameFeaturedRequest;
import com.trophix.api.admin.infrastructure.adapter.in.dto.UpdateGuideRequest;
import com.trophix.api.admin.infrastructure.adapter.in.dto.UpdateUserRolesRequest;
import com.trophix.api.admin.infrastructure.adapter.in.mapper.AdminWebMapper;
import com.trophix.api.ai.application.ports.in.RequestGuideAiGenerationUseCase;
import com.trophix.api.games.application.ports.in.ImportGameUseCase;
import com.trophix.api.guides.application.ports.in.ReviewGuideUseCase;
import com.trophix.api.guides.infrastructure.adapter.in.dto.GuideResponse;
import com.trophix.api.shared.dto.MessageResponse;
import com.trophix.api.guides.infrastructure.adapter.in.mapper.GuideWebMapper;
import com.trophix.api.guides.model.GuideStatus;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * Admin area. Every route is protected at the security layer
 * ({@code /api/admin/**} requires {@code ROLE_ADMIN}); the controller only
 * orchestrates use cases.
 */
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final GetDashboardStatsUseCase getDashboardStatsUseCase;
    private final GetSidecarStatusUseCase getSidecarStatusUseCase;
    private final GetSystemHealthUseCase getSystemHealthUseCase;
    private final ListAdminUsersUseCase listAdminUsersUseCase;
    private final UpdateUserRolesUseCase updateUserRolesUseCase;
    private final GetPendingGuidesUseCase getPendingGuidesUseCase;
    private final ListAllGuidesUseCase listAllGuidesUseCase;
    private final UpdateGuideUseCase updateGuideUseCase;
    private final DeleteGuideUseCase deleteGuideUseCase;
    private final ReviewGuideUseCase reviewGuideUseCase;
    private final SetGameFeaturedUseCase setGameFeaturedUseCase;
    private final ImportGameUseCase importGameUseCase;
    private final RequestGuideAiGenerationUseCase requestGuideAiGenerationUseCase;
    private final AdminWebMapper adminWebMapper;
    private final GuideWebMapper guideWebMapper;

    @GetMapping("/dashboard/stats")
    public ResponseEntity<AdminDashboardStatsResponse> getDashboardStats() {
        GetDashboardStatsUseCase.DashboardStats stats = getDashboardStatsUseCase.getStats();
        return ResponseEntity.ok(adminWebMapper.toDashboardStatsResponse(stats));
    }

    @GetMapping("/sidecar/status")
    public ResponseEntity<SidecarStatusResponse> getSidecarStatus() {
        GetSidecarStatusUseCase.SidecarStatus status = getSidecarStatusUseCase.getStatus();
        HttpStatus httpStatus = status.up() ? HttpStatus.OK : HttpStatus.SERVICE_UNAVAILABLE;
        return ResponseEntity.status(httpStatus).body(adminWebMapper.toSidecarStatusResponse(status));
    }

    @GetMapping("/system/health")
    public ResponseEntity<SystemHealthResponse> getSystemHealth() {
        GetSystemHealthUseCase.SystemHealth health = getSystemHealthUseCase.getHealth();
        return ResponseEntity.ok(adminWebMapper.toSystemHealthResponse(health));
    }

    @GetMapping("/users")
    public ResponseEntity<Page<AdminUserResponse>> listUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String role) {
        Page<AdminUserResponse> users = listAdminUsersUseCase
                .listUsers(search, role, PageRequest.of(Math.max(page, 0), Math.min(size, 100)))
                .map(adminWebMapper::toAdminUserResponse);
        return ResponseEntity.ok(users);
    }

    @PutMapping("/users/{userId}/roles")
    public ResponseEntity<AdminUserResponse> updateRoles(
            @PathVariable UUID userId,
            @Valid @RequestBody UpdateUserRolesRequest request) {
        UpdateUserRolesUseCase.UpdateUserRolesCommand command =
                new UpdateUserRolesUseCase.UpdateUserRolesCommand(userId, request.roles());
        return ResponseEntity.ok(adminWebMapper.toAdminUserResponse(
                updateUserRolesUseCase.updateRoles(command)));
    }

    @GetMapping("/guides/pending")
    public ResponseEntity<Page<ModerationGuideResponse>> getPendingGuides(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<ModerationGuideResponse> guides = getPendingGuidesUseCase
                .getPendingGuides(PageRequest.of(Math.max(page, 0), Math.min(size, 100)))
                .map(adminWebMapper::toModerationGuideResponse);
        return ResponseEntity.ok(guides);
    }

    @GetMapping("/guides")
    public ResponseEntity<Page<GuideResponse>> listAllGuides(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) GuideStatus status,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Boolean isTrophyGuide) {
        Page<GuideResponse> guides = listAllGuidesUseCase
                .listAllGuides(status, search, isTrophyGuide, PageRequest.of(Math.max(page, 0), Math.min(size, 100)))
                .map(guideWebMapper::toGuideResponse);
        return ResponseEntity.ok(guides);
    }

    @PostMapping("/guides/{guideId}/approve")
    public ResponseEntity<MessageResponse> approve(
            @AuthenticationPrincipal String adminId,
            @PathVariable UUID guideId) {
        String message = reviewGuideUseCase.review(UUID.fromString(adminId), guideId,
                ReviewGuideUseCase.ReviewAction.APPROVE);
        return ResponseEntity.ok(new MessageResponse(message));
    }

    @PutMapping("/guides/{guideId}")
    public ResponseEntity<MessageResponse> updateGuide(
            @PathVariable UUID guideId,
            @Valid @RequestBody UpdateGuideRequest request) {
        updateGuideUseCase.update(adminWebMapper.toUpdateGuideCommand(guideId, request));
        return ResponseEntity.ok(new MessageResponse("Guia atualizado com sucesso."));
    }

    @DeleteMapping("/guides/{guideId}")
    public ResponseEntity<Void> deleteGuide(@PathVariable UUID guideId) {
        deleteGuideUseCase.delete(guideId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PostMapping("/guides/{guideId}/reject")
    public ResponseEntity<MessageResponse> reject(
            @AuthenticationPrincipal String adminId,
            @PathVariable UUID guideId) {
        String message = reviewGuideUseCase.review(UUID.fromString(adminId), guideId,
                ReviewGuideUseCase.ReviewAction.REJECT);
        return ResponseEntity.ok(new MessageResponse(message));
    }

    @PatchMapping("/games/{gameId}/feature")
    public ResponseEntity<MessageResponse> updateGameFeatured(
            @PathVariable UUID gameId,
            @Valid @RequestBody UpdateGameFeaturedRequest request) {
        setGameFeaturedUseCase.execute(adminWebMapper.toSetGameFeaturedCommand(gameId, request));
        return ResponseEntity.ok(new MessageResponse("Destaque do jogo atualizado com sucesso."));
    }

    @PostMapping("/games/import")
    public ResponseEntity<MessageResponse> importGame(
            @AuthenticationPrincipal String adminId,
            @RequestParam String npCommunicationId) {
        importGameUseCase.execute(new ImportGameUseCase.ImportGameCommand(npCommunicationId, UUID.fromString(adminId)));
        return ResponseEntity.ok(new MessageResponse("Jogo importado da PSN com sucesso."));
    }

    @PostMapping("/guides/{guideId}/generate-ai")
    public ResponseEntity<Void> generateGuideAi(@PathVariable UUID guideId) {
        requestGuideAiGenerationUseCase.requestRoadmapGeneration(guideId);
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/guides/{guideId}/trophies/{trophyId}/generate-ai")
    public ResponseEntity<Void> generateTrophyAi(
            @PathVariable UUID guideId,
            @PathVariable UUID trophyId) {
        requestGuideAiGenerationUseCase.requestTrophyTipGeneration(guideId, trophyId);
        return ResponseEntity.accepted().build();
    }
}
