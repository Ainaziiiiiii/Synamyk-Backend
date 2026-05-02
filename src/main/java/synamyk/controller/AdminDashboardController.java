package synamyk.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import synamyk.dto.admin.DashboardResponse;
import synamyk.service.AdminDashboardService;

@RestController
@RequestMapping("/api/admin/dashboard")
@RequiredArgsConstructor
@Tag(name = "Админ — Dashboard", description = "Сводная статистика для главной страницы. Требуется роль ADMIN.")
@SecurityRequirement(name = "Bearer")
public class AdminDashboardController {

    private final AdminDashboardService dashboardService;

    @GetMapping
    @Operation(summary = "Получить статистику Dashboard",
            description = """
                    Возвращает:
                    - **activeUsers** — активные пользователи за последние 24ч
                    - **completedTests** — пройдено тестов за 24ч
                    - **newRegistrations** — новых пользователей сегодня
                    - **weeklyRevenue** — доход за последние 7 дней (сом)
                    - **activityChart** — данные для графика активности
                    - **successChart** — успешность по предметам
                    """)
    public ResponseEntity<DashboardResponse> getDashboard() {
        return ResponseEntity.ok(dashboardService.getDashboard());
    }
}
