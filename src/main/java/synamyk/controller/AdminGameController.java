package synamyk.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
@Tag(name = "Админ — Игры", description = """
        Управление игровыми тестами и просмотр отчётов по сыгранным партиям.
        Требуется роль **ADMIN**.

        **Игровые тесты** — отдельная сущность от обычных тестов (тестирование ОРТ).
        Каждый тест содержит вопросы с 2–6 вариантами ответа, ровно один из которых правильный.
        """)
@SecurityRequirement(name = "Bearer")
public class AdminGameController {

    private final AdminGameService adminGameService;

    // ===== ТЕСТЫ =====

    @GetMapping
    @Operation(
        summary = "Список всех игровых тестов",
        description = "Возвращает все тесты (активные и неактивные) без вопросов. Для получения вопросов — GET /{id}."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Список тестов"),
        @ApiResponse(responseCode = "401", description = "Не авторизован", content = @Content),
        @ApiResponse(responseCode = "403", description = "Требуется роль ADMIN", content = @Content)
    })
    public ResponseEntity<List<GameTestResponse>> listAll() {
        return ResponseEntity.ok(adminGameService.listAll());
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Получить тест с вопросами",
        description = "Возвращает полные данные теста включая все вопросы, варианты ответа и флаги correct."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Тест найден"),
        @ApiResponse(responseCode = "404", description = "Тест не найден", content = @Content),
        @ApiResponse(responseCode = "403", description = "Требуется роль ADMIN", content = @Content)
    })
    public ResponseEntity<GameTestResponse> getById(
        @Parameter(description = "ID игрового теста", example = "1", required = true)
        @PathVariable Long id
    ) {
        return ResponseEntity.ok(adminGameService.getById(id));
    }

    @PostMapping
    @Operation(
        summary = "Создать игровой тест",
        description = """
            Создаёт новый игровой тест. Вопросы можно добавить сразу в теле запроса или позже через `POST /{id}/questions`.

            **Параметры timeLimitSeconds и questionsPerGame:**
            - `timeLimitSeconds` — сколько секунд даётся на один вопрос (минимум 5, по умолчанию 30)
            - `questionsPerGame` — сколько вопросов берётся за одну партию в случайном порядке (0 = все вопросы)
            """
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Тест создан",
            content = @Content(schema = @Schema(implementation = GameTestResponse.class),
                examples = @ExampleObject(value = """
                    {
                      "id": 1,
                      "title": "Математика: Арифметика",
                      "description": "Базовые вопросы по арифметике",
                      "timeLimitSeconds": 30,
                      "questionsPerGame": 10,
                      "active": true,
                      "questionCount": 0,
                      "questions": []
                    }
                    """))),
        @ApiResponse(responseCode = "400", description = "Ошибка валидации", content = @Content),
        @ApiResponse(responseCode = "403", description = "Требуется роль ADMIN", content = @Content)
    })
    public ResponseEntity<GameTestResponse> create(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Данные нового теста",
            content = @Content(schema = @Schema(implementation = CreateGameTestRequest.class),
                examples = @ExampleObject(value = """
                    {
                      "title": "Математика: Арифметика",
                      "description": "Базовые вопросы",
                      "timeLimitSeconds": 30,
                      "questionsPerGame": 5,
                      "questions": [
                        {
                          "text": "Чему равно среднее арифметическое 10, 20, 30?",
                          "options": [
                            { "text": "10", "correct": false },
                            { "text": "20", "correct": true },
                            { "text": "30", "correct": false },
                            { "text": "60", "correct": false }
                          ]
                        }
                      ]
                    }
                    """)))
        @Valid @RequestBody CreateGameTestRequest req
    ) {
        return ResponseEntity.ok(adminGameService.createGameTest(req));
    }

    @PutMapping("/{id}")
    @Operation(
        summary = "Обновить игровой тест",
        description = "Обновляет название, описание и параметры теста. Вопросы не затрагиваются."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Тест обновлён"),
        @ApiResponse(responseCode = "400", description = "Ошибка валидации", content = @Content),
        @ApiResponse(responseCode = "404", description = "Тест не найден", content = @Content),
        @ApiResponse(responseCode = "403", description = "Требуется роль ADMIN", content = @Content)
    })
    public ResponseEntity<GameTestResponse> update(
        @Parameter(description = "ID игрового теста", example = "1", required = true)
        @PathVariable Long id,
        @Valid @RequestBody CreateGameTestRequest req
    ) {
        return ResponseEntity.ok(adminGameService.updateGameTest(id, req));
    }

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Деактивировать игровой тест",
        description = "Мягкое удаление: тест скрывается из списка клиентов. Данные и отчёты сохраняются."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Тест деактивирован", content = @Content),
        @ApiResponse(responseCode = "404", description = "Тест не найден", content = @Content),
        @ApiResponse(responseCode = "403", description = "Требуется роль ADMIN", content = @Content)
    })
    public ResponseEntity<Void> delete(
        @Parameter(description = "ID игрового теста", example = "1", required = true)
        @PathVariable Long id
    ) {
        adminGameService.deleteGameTest(id);
        return ResponseEntity.noContent().build();
    }

    // ===== ВОПРОСЫ =====

    @PostMapping("/{id}/questions")
    @Operation(
        summary = "Добавить вопрос к тесту",
        description = """
            Добавляет один вопрос к существующему тесту.
            Правила для вариантов ответа:
            - От **2 до 6** вариантов
            - Ровно **один** вариант должен иметь `correct: true`
            """
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Вопрос добавлен, возвращает обновлённый тест"),
        @ApiResponse(responseCode = "400", description = "Ошибка валидации (нет правильного ответа, неверное число вариантов)", content = @Content),
        @ApiResponse(responseCode = "404", description = "Тест не найден", content = @Content),
        @ApiResponse(responseCode = "403", description = "Требуется роль ADMIN", content = @Content)
    })
    public ResponseEntity<GameTestResponse> addQuestion(
        @Parameter(description = "ID игрового теста", example = "1", required = true)
        @PathVariable Long id,
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Данные нового вопроса",
            content = @Content(schema = @Schema(implementation = CreateGameQuestionRequest.class),
                examples = @ExampleObject(value = """
                    {
                      "text": "Чему равно среднее арифметическое чисел 10, 20 и 30?",
                      "options": [
                        { "text": "10", "correct": false },
                        { "text": "20", "correct": true },
                        { "text": "30", "correct": false },
                        { "text": "60", "correct": false }
                      ]
                    }
                    """)))
        @Valid @RequestBody CreateGameQuestionRequest req
    ) {
        return ResponseEntity.ok(adminGameService.addQuestionToTest(id, req));
    }

    @DeleteMapping("/questions/{questionId}")
    @Operation(
        summary = "Деактивировать вопрос",
        description = "Скрывает вопрос из новых партий. Старые результаты не затрагиваются."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Вопрос деактивирован", content = @Content),
        @ApiResponse(responseCode = "404", description = "Вопрос не найден", content = @Content),
        @ApiResponse(responseCode = "403", description = "Требуется роль ADMIN", content = @Content)
    })
    public ResponseEntity<Void> deleteQuestion(
        @Parameter(description = "ID вопроса", example = "42", required = true)
        @PathVariable Long questionId
    ) {
        adminGameService.deleteQuestion(questionId);
        return ResponseEntity.noContent().build();
    }

    // ===== ОТЧЁТ =====

    @GetMapping("/{id}/report")
    @Operation(
        summary = "Отчёт по игровому тесту",
        description = """
            Возвращает статистику и список всех завершённых партий по данному тесту.

            **Содержимое отчёта:**
            - Общее число сыгранных партий
            - Число уникальных игроков
            - Для каждой партии: имена игроков, счёт, имя победителя, дата и время
            """
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Отчёт сформирован",
            content = @Content(schema = @Schema(implementation = AdminGameReportResponse.class),
                examples = @ExampleObject(value = """
                    {
                      "gameTestId": 1,
                      "gameTestTitle": "Математика: Арифметика",
                      "totalGames": 3,
                      "totalPlayers": 5,
                      "games": [
                        {
                          "roomId": 7,
                          "playedAt": "2026-05-11T14:30:00",
                          "player1Name": "Айнур Токтоматова",
                          "player2Name": "Бекзат",
                          "player1Score": 7,
                          "player2Score": 5,
                          "winnerName": "Айнур Токтоматова",
                          "totalQuestions": 10
                        }
                      ]
                    }
                    """))),
        @ApiResponse(responseCode = "404", description = "Тест не найден", content = @Content),
        @ApiResponse(responseCode = "403", description = "Требуется роль ADMIN", content = @Content)
    })
    public ResponseEntity<AdminGameReportResponse> getReport(
        @Parameter(description = "ID игрового теста", example = "1", required = true)
        @PathVariable Long id
    ) {
        return ResponseEntity.ok(adminGameService.getReport(id));
    }
}
