package synamyk.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import synamyk.dto.NewsDetailResponse;
import synamyk.dto.admin.CreateNewsRequest;
import synamyk.service.NewsService;

@RestController
@RequestMapping("/api/admin/news")
@RequiredArgsConstructor
@Tag(name = "Админ — Новости", description = "Управление новостными статьями. Требуется роль ADMIN.")
@SecurityRequirement(name = "Bearer")
public class AdminFeedController {

    private final NewsService newsService;

    @PostMapping
    @Operation(summary = "Создать новостную статью",
            description = "Поля `titleKy` и `contentKy` необязательны — если указаны, статья будет отображаться на кыргызском языке.")
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
    @Operation(summary = "Деактивировать (скрыть) новостную статью", description = "Мягкое удаление: статья скрывается из ленты пользователей.")
    public ResponseEntity<Void> delete(@Parameter(description = "ID новости") @PathVariable Long id) {
        newsService.deleteNews(id);
        return ResponseEntity.noContent().build();
    }
}