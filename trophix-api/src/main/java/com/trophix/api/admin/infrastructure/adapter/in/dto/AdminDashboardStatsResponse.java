package com.trophix.api.admin.infrastructure.adapter.in.dto;

/**
 * Metrics for the admin overview dashboard, with trend deltas and the
 * human-readable labels rendered by the web layer.
 */
public record AdminDashboardStatsResponse(
        int newUsersCount,
        int newUsersTrend,
        String newUsersTrendText,
        int pendingGuidesCount,
        int pendingGuidesTrend,
        String pendingGuidesTrendText,
        int syncsCount,
        String syncsTrendText,
        boolean syncsTrendPositive,
        int reportsCount,
        int reportsTrend,
        String reportsTrendText) {
}
