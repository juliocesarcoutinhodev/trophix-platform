package com.trophix.api.admin.application.ports.in;

/**
 * Counters for the admin overview dashboard.
 */
public interface GetDashboardStatsUseCase {

    DashboardStats getStats();

    record DashboardStats(long newUsersToday, long pendingGuides, long openReports) {
    }
}
