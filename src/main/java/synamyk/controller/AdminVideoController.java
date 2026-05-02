package synamyk.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import synamyk.dto.VideoLessonResponse;
import synamyk.dto.admin.AdminVideoListResponse;
import synamyk.dto.admin.CreateVideoLessonRequest;
import synamyk.service.AdminVideoService;
import synamyk.service.AnalyticsService;

@RestController
@RequestMapping("/api/admin/videos")
@RequiredArgsConstructor
@Tag(name = "Админ — Видеоуроки", description = "Управление видеоуроками. Видео хранятся как YouTube-ссылки. Требуется роль ADMIN")
@SecurityRequirement(name = "Bearer")
public class AdminVideoController {

    private final AnalyticsService analyticsService;
    private final AdminVideoService adminVideoService;

    @GetMapping
    @Operation(summary = "Список всех видеоуроков (с пагинацией и фильтрами)")
    public ResponseEntity<Page<AdminVideoListResponse>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long testId,
            @RequestParam(required = false) Boolean active) {
        return ResponseEntity.ok(adminVideoService.list(page, size, search, testId, active));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Переключить статус видеоурока")
    public ResponseEntity<AdminVideoListResponse> toggleStatus(@PathVariable Long id) {
        return ResponseEntity.ok(adminVideoService.toggleStatus(id));
    }

    @PostMapping
    @Operation(summary = "Создать видеоурок",
            description = "Поле `videoUrl` — ссылка на YouTube. Превью загружается через POST /api/upload (тип VIDEO_THUMBNAIL)")
    public ResponseEntity<VideoLessonResponse> create(@Valid @RequestBody CreateVideoLessonRequest request) {
        return ResponseEntity.ok(analyticsService.createVideo(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить видеоурок")
    public ResponseEntity<VideoLessonResponse> update(
            @Parameter(description = "ID видеоурока") @PathVariable Long id,
            @Valid @RequestBody CreateVideoLessonRequest request) {
        return ResponseEntity.ok(analyticsService.updateVideo(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Деактивировать видеоурок")
    public ResponseEntity<Void> delete(@Parameter(description = "ID видеоурока") @PathVariable Long id) {
        analyticsService.deleteVideo(id);
        return ResponseEntity.noContent().build();
    }
}