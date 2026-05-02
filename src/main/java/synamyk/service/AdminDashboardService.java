package synamyk.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import synamyk.dto.admin.DashboardResponse;
import synamyk.entities.Payment;
import synamyk.repo.PaymentRepository;
import synamyk.repo.TestSessionRepository;
import synamyk.repo.UserRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminDashboardService {

    private final UserRepository userRepo;
    private final TestSessionRepository sessionRepo;
    private final PaymentRepository paymentRepo;

    public DashboardResponse getDashboard() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime last24h = now.minusHours(24);
        LocalDateTime startOfWeek = now.minusDays(7);
        LocalDateTime startOfDay = now.toLocalDate().atStartOfDay();

        long activeUsers = userRepo.countActiveUsersAfter(last24h);
        long completedTests = sessionRepo.countCompletedAfter(last24h);
        long newRegistrations = userRepo.countRegisteredAfter(startOfDay);
        BigDecimal weeklyRevenue = paymentRepo.sumRevenueAfter(startOfWeek, Payment.PaymentStatus.COMPLETED);

        return DashboardResponse.builder()
                .activeUsers(activeUsers)
                .completedTests(completedTests)
                .newRegistrations(newRegistrations)
                .weeklyRevenue(weeklyRevenue)
                .activityChart(buildActivityChart(last24h))
                .successChart(buildSuccessChart())
                .build();
    }

    private List<DashboardResponse.ActivityBar> buildActivityChart(LocalDateTime from) {
        long registrations = userRepo.countRegisteredAfter(from);
        long tests = sessionRepo.countCompletedAfter(from);
        long payments = paymentRepo.countCompletedAfter(from, Payment.PaymentStatus.COMPLETED);

        return List.of(
                DashboardResponse.ActivityBar.builder().label("Регистрация").count(registrations).build(),
                DashboardResponse.ActivityBar.builder().label("Тест").count(tests).build(),
                DashboardResponse.ActivityBar.builder().label("Онлайн-игра").count(0).build(),
                DashboardResponse.ActivityBar.builder().label("Продажи").count(payments).build()
        );
    }

    private List<DashboardResponse.SuccessBar> buildSuccessChart() {
        return sessionRepo.findSuccessRateBySubject().stream()
                .map(r -> DashboardResponse.SuccessBar.builder()
                        .subject(r[0] != null ? r[0].toString() : "Другое")
                        .successRate(r[1] != null ? ((Number) r[1]).doubleValue() : 0.0)
                        .build())
                .collect(Collectors.toList());
    }
}
