package com.trophix.api.admin.infrastructure.adapter.in;

import com.trophix.api.admin.application.ports.in.GetDashboardStatsUseCase;
import com.trophix.api.admin.application.ports.in.GetPendingGuidesUseCase;
import com.trophix.api.admin.application.ports.in.ListAdminUsersUseCase;
import com.trophix.api.admin.application.ports.in.UpdateGuideUseCase;
import com.trophix.api.admin.application.ports.in.UpdateUserRolesUseCase;
import com.trophix.api.admin.infrastructure.adapter.in.dto.AdminUserResponse;
import com.trophix.api.admin.infrastructure.adapter.in.dto.DashboardStatsResponse;
import com.trophix.api.admin.infrastructure.adapter.in.dto.ModerationGuideResponse;
import com.trophix.api.admin.infrastructure.adapter.in.dto.UpdateGuideRequest;
import com.trophix.api.admin.infrastructure.adapter.in.dto.UpdateUserRolesRequest;
import com.trophix.api.admin.infrastructure.adapter.in.mapper.AdminWebMapper;
import com.trophix.api.guides.application.ports.in.ReviewGuideUseCase;
import com.trophix.api.guides.infrastructure.adapter.in.dto.MessageResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
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
    private final ListAdminUsersUseCase listAdminUsersUseCase;
    private final UpdateUserRolesUseCase updateUserRolesUseCase;
    private final GetPendingGuidesUseCase getPendingGuidesUseCase;
    private final UpdateGuideUseCase updateGuideUseCase;
    private final ReviewGuideUseCase reviewGuideUseCase;
    private final AdminWebMapper adminWebMapper;

    @GetMapping("/dashboard/stats")
    public ResponseEntity<DashboardStatsResponse> getDashboardStats() {
        GetDashboardStatsUseCase.DashboardStats stats = getDashboardStatsUseCase.getStats();
        return ResponseEntity.ok(adminWebMapper.toDashboardStatsResponse(stats));
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

    @PostMapping("/guides/{guideId}/reject")
    public ResponseEntity<MessageResponse> reject(
            @AuthenticationPrincipal String adminId,
            @PathVariable UUID guideId) {
        String message = reviewGuideUseCase.review(UUID.fromString(adminId), guideId,
                ReviewGuideUseCase.ReviewAction.REJECT);
        return ResponseEntity.ok(new MessageResponse(message));
    }
}
