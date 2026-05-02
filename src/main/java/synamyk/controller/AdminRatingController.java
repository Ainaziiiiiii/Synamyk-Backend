package synamyk.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import synamyk.dto.MessageResponse;
import synamyk.dto.admin.AdminRatingEntryResponse;
import synamyk.service.AdminRatingService;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/admin/rating")
@RequiredArgsConstructor
@Tag(name = "Админ — Рейтинг", description = "Просмотр и управление рейтингом пользователей. Требуется роль ADMIN.")
@SecurityRequirement(name = "Bearer")
public class AdminRatingController {

    private final AdminRatingService adminRatingService;

    @GetMapping
    @Operation(summary = "Рейтинг пользователей",
            description = "Глобальный рейтинг или по конкретному тесту. Фильтры: testId, dateFrom, dateTo.")
    public ResponseEntity<Page<AdminRatingEntryResponse>> getRating(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) Long testId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo) {
        return ResponseEntity.ok(adminRatingService.getRating(page, size, testId, dateFrom, dateTo));
    }

    @PostMapping("/reset")
    @Operation(summary = "Сбросить рейтинг",
            description = "Обнуляет earnedPoints во всех завершённых сессиях. Действие необратимо.")
    public ResponseEntity<MessageResponse> resetRating() {
        adminRatingService.resetRating();
        return ResponseEntity.ok(MessageResponse.builder().success(true).message("Rating reset successfully.").build());
    }
}
