package synamyk.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import synamyk.dto.NewsDetailResponse;
import synamyk.dto.admin.AdminNewsListResponse;
import synamyk.dto.admin.CreateNewsRequest;
import synamyk.service.AdminNewsService;
import synamyk.service.NewsService;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/admin/news")
@RequiredArgsConstructor
@Tag(name = "Админ — Новости", description = "Управление новостными статьями. Требуется роль ADMIN.")
@SecurityRequirement(name = "Bearer")
public class AdminFeedController {

    private final NewsService newsService;
    private final AdminNewsService adminNewsService;

    @GetMapping
    @Operation(summary = "Список всех новостей (с пагинацией и фильтрами)",
            description = "Фильтры: search, type, active, dateFrom, dateTo")
    public ResponseEntity<Page<AdminNewsListResponse>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Boolean active,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo) {
        return ResponseEntity.ok(adminNewsService.list(page, size, search, type, active, dateFrom, dateTo));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Переключить статус новости (активна/скрыта)")
    public ResponseEntity<AdminNewsListResponse> toggleStatus(@PathVariable Long id) {
        return ResponseEntity.ok(adminNewsService.toggleStatus(id));
    }

    @PostMapping
    @Operation(summary = "Создать новостную статью")
    public ResponseEntity<NewsDetailResponse> create(@Valid @RequestBody CreateNewsRequest request) {
        return ResponseEntity.ok(newsService.createNews(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить новостную статью")
    public ResponseEntity<NewsDetailResponse> update(
            @Parameter(description = "ID новости") @PathVariable Long id,
            @Valid @RequestBody CreateNewsRequest request) {
        return ResponseEntity.ok(newsService.updateNews(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Деактивировать (скрыть) новостную статью", description = "Мягкое удаление: статья скрывается из ленты пользователей")
    public ResponseEntity<Void> delete(@Parameter(description = "ID новости") @PathVariable Long id) {
        newsService.deleteNews(id);
        return ResponseEntity.noContent().build();
    }
}