package synamyk.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import synamyk.dto.*;
import synamyk.service.NewsService;
import synamyk.service.RatingService;

import java.util.List;

@RestController
@RequestMapping("/api/feed")
@RequiredArgsConstructor
@Tag(name = "Лента", description = "Публичные эндпоинты — JWT не требуется. Передайте ?lang=KY для контента на кыргызском.")
public class FeedController {

    private final RatingService ratingService;
    private final NewsService newsService;

    // ===== РЕЙТИНГ =====

    @GetMapping("/rating/filters")
    @Operation(
            summary = "Список тестов для фильтра рейтинга",
            description = "Возвращает все активные тесты для заполнения выпадающего списка на экране рейтинга."
    )
    @ApiResponse(responseCode = "200", description = "Список тестов")
    public ResponseEntity<List<TestListResponse>> getRatingFilters(
            @Parameter(description = "Язык интерфейса: RU (по умолчанию) или KY")
            @RequestParam(defaultValue = "RU") String lang) {
        return ResponseEntity.ok(ratingService.getFilterOptions(lang));
    }

    @GetMapping("/rating/{testId}")
    @Operation(
            summary = "Рейтинг (таблица лидеров) по тесту",
            description = "Возвращает ранжированный список пользователей по выбранному тесту. " +
                    "Балл = максимальное количество правильных ответов за одну завершённую сессию. " +
                    "Пользователи с одинаковым баллом получают одинаковый ранг."
    )
    @ApiResponse(responseCode = "200", description = "Таблица лидеров, отсортированная по убыванию баллов")
    public ResponseEntity<RatingResponse> getRating(
            @Parameter(description = "ID теста") @PathVariable Long testId,
            @Parameter(description = "Язык интерфейса: RU (по умолчанию) или KY")
            @RequestParam(defaultValue = "RU") String lang) {
        return ResponseEntity.ok(ratingService.getRatingByTest(testId, lang));
    }

    // ===== НОВОСТИ =====

    @GetMapping("/news")
    @Operation(
            summary = "Список новостей",
            description = "Возвращает все активные новостные статьи, отсортированные по дате публикации (сначала новые). " +
                    "Каждая запись содержит превью — первые 150 символов текста."
    )
    @ApiResponse(responseCode = "200", description = "Список новостных статей")
    public ResponseEntity<List<NewsListResponse>> getNewsList(
            @Parameter(description = "Язык интерфейса: RU (по умолчанию) или KY")
            @RequestParam(defaultValue = "RU") String lang) {
        return ResponseEntity.ok(newsService.getNewsList(lang));
    }

    @GetMapping("/news/{id}")
    @Operation(
            summary = "Полная новостная статья",
            description = "Возвращает полный текст новостной статьи по её ID."
    )
    @ApiResponse(responseCode = "200", description = "Полная статья")
    public ResponseEntity<NewsDetailResponse> getNewsDetail(
            @Parameter(description = "ID новости") @PathVariable Long id,
            @Parameter(description = "Язык интерфейса: RU (по умолчанию) или KY")
            @RequestParam(defaultValue = "RU") String lang) {
        return ResponseEntity.ok(newsService.getNewsDetail(id, lang));
    }
}