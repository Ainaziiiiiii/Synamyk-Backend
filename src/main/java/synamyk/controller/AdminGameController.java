package synamyk.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import synamyk.dto.game.*;
import synamyk.service.AdminGameService;

import java.util.List;

@RestController
@RequestMapping("/api/admin/game-tests")
@RequiredArgsConstructor
@Tag(name = "Админ — Игры", description = "Управление игровыми тестами и просмотр отчётов. Требуется роль ADMIN.")
@SecurityRequirement(name = "Bearer")
public class AdminGameController {

    private final AdminGameService adminGameService;

    @GetMapping
    @Operation(summary = "Список всех игровых тестов")
    public ResponseEntity<List<GameTestResponse>> listAll() {
        return ResponseEntity.ok(adminGameService.listAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить игровой тест с вопросами")
    public ResponseEntity<GameTestResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(adminGameService.getById(id));
    }

    @PostMapping
    @Operation(summary = "Создать игровой тест (с вопросами или без)")
    public ResponseEntity<GameTestResponse> create(@Valid @RequestBody CreateGameTestRequest req) {
        return ResponseEntity.ok(adminGameService.createGameTest(req));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить игровой тест")
    public ResponseEntity<GameTestResponse> update(@PathVariable Long id,
                                                    @Valid @RequestBody CreateGameTestRequest req) {
        return ResponseEntity.ok(adminGameService.updateGameTest(id, req));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Деактивировать игровой тест")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        adminGameService.deleteGameTest(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/questions")
    @Operation(summary = "Добавить вопрос к игровому тесту")
    public ResponseEntity<GameTestResponse> addQuestion(@PathVariable Long id,
                                                         @Valid @RequestBody CreateGameQuestionRequest req) {
        return ResponseEntity.ok(adminGameService.addQuestionToTest(id, req));
    }

    @DeleteMapping("/questions/{questionId}")
    @Operation(summary = "Деактивировать вопрос")
    public ResponseEntity<Void> deleteQuestion(@PathVariable Long questionId) {
        adminGameService.deleteQuestion(questionId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/report")
    @Operation(summary = "Отчёт по игровому тесту — все сыгранные партии")
    public ResponseEntity<AdminGameReportResponse> getReport(@PathVariable Long id) {
        return ResponseEntity.ok(adminGameService.getReport(id));
    }
}
