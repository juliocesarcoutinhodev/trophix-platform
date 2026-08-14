package com.trophix.api.admin.infrastructure.adapter.in.dto;

/**
 * Overview counters for the admin dashboard.
 */
public record DashboardStatsResponse(
        long newUsersToday,
        long pendingGuides,
        long openReports) {
}
