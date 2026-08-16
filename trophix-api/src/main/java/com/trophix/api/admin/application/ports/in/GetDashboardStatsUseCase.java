package com.trophix.api.admin.application.ports.in;

/**
 * Metrics for the admin overview dashboard: counters and day-over-day trends
 * for new users, pending guides, PSN syncs and open reports.
 */
public interface GetDashboardStatsUseCase {

    DashboardStats getStats();

    record DashboardStats(
            int newUsersCount,
            int newUsersTrend,
            int pendingGuidesCount,
            int pendingGuidesTrend,
            int syncsCount,
            boolean syncsTrendPositive,
            int reportsCount,
            int reportsTrend) {
    }
}
