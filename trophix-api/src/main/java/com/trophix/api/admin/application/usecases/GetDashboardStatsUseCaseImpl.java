package com.trophix.api.admin.application.usecases;

import com.trophix.api.admin.application.ports.in.GetDashboardStatsUseCase;
import com.trophix.api.guides.application.ports.out.GuideRepositoryPort;
import com.trophix.api.guides.model.GuideStatus;
import com.trophix.api.reports.application.ports.out.ReportRepository;
import com.trophix.api.reports.model.ReportStatus;
import com.trophix.api.users.application.ports.out.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

@Component
@Slf4j
@RequiredArgsConstructor
public class GetDashboardStatsUseCaseImpl implements GetDashboardStatsUseCase {

    private static final Duration SYNC_WINDOW = Duration.ofHours(24);

    private final UserRepository userRepository;
    private final GuideRepositoryPort guideRepository;
    private final ReportRepository reportRepository;

    @Override
    @Transactional(readOnly = true)
    public DashboardStats getStats() {
        ZoneId zone = ZoneId.systemDefault();
        Instant startOfToday = LocalDate.now(zone).atStartOfDay(zone).toInstant();
        Instant startOfYesterday = startOfToday.minus(Duration.ofDays(1));

        int newUsersCount = toInt(userRepository.countCreatedSince(startOfToday));
        int newUsersTrend = newUsersCount - toInt(userRepository.countCreatedSince(startOfYesterday) - newUsersCount);

        int pendingGuidesCount = toInt(guideRepository.countByStatus(GuideStatus.PENDING));
        int pendingToday = toInt(guideRepository.countByStatusSince(GuideStatus.PENDING, startOfToday));
        int pendingYesterday = toInt(guideRepository.countByStatusSince(GuideStatus.PENDING, startOfYesterday)) - pendingToday;
        int pendingGuidesTrend = pendingToday - pendingYesterday;

        Instant now = Instant.now();
        int syncsCount = userRepository.findActiveUserIds(now.minus(SYNC_WINDOW)).size();
        int prevSyncs = userRepository.findActiveUserIds(now.minus(SYNC_WINDOW.multipliedBy(2))).size() - syncsCount;
        boolean syncsTrendPositive = syncsCount >= prevSyncs;

        int reportsCount = toInt(reportRepository.countByStatus(ReportStatus.OPEN));
        int reportsToday = toInt(reportRepository.countByStatusSince(ReportStatus.OPEN, startOfToday));
        int reportsYesterday = toInt(reportRepository.countByStatusSince(ReportStatus.OPEN, startOfYesterday)) - reportsToday;
        int reportsTrend = reportsToday - reportsYesterday;

        log.info("Dashboard stats: novosUsuarios={} guiasPendentes={} sincronizacoes={} denunciasAbertas={}",
                newUsersCount, pendingGuidesCount, syncsCount, reportsCount);
        return new DashboardStats(newUsersCount, newUsersTrend, pendingGuidesCount, pendingGuidesTrend,
                syncsCount, syncsTrendPositive, reportsCount, reportsTrend);
    }

    private int toInt(long value) {
        return Math.toIntExact(value);
    }
}
