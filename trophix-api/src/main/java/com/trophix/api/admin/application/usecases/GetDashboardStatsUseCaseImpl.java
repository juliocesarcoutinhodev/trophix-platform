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

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

@Component
@Slf4j
@RequiredArgsConstructor
public class GetDashboardStatsUseCaseImpl implements GetDashboardStatsUseCase {

    private final UserRepository userRepository;
    private final GuideRepositoryPort guideRepository;
    private final ReportRepository reportRepository;

    @Override
    @Transactional(readOnly = true)
    public DashboardStats getStats() {
        Instant startOfToday = LocalDate.now(ZoneId.systemDefault())
                .atStartOfDay(ZoneId.systemDefault()).toInstant();

        long newUsersToday = userRepository.countCreatedSince(startOfToday);
        long pendingGuides = guideRepository.countByStatus(GuideStatus.PENDING);
        long openReports = reportRepository.countByStatus(ReportStatus.OPEN);

        log.info("Dashboard stats: novosUsuariosHoje={} guiasPendentes={} denunciasAbertas={}",
                newUsersToday, pendingGuides, openReports);
        return new DashboardStats(newUsersToday, pendingGuides, openReports);
    }
}
