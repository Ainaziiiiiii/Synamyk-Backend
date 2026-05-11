package synamyk.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import synamyk.dto.game.JoinGameResponse;
import synamyk.entities.User;
import synamyk.service.GameService;

import java.util.List;

@RestController
@RequestMapping("/api/game")
@RequiredArgsConstructor
@Tag(name = "Игра", description = "Клиентские эндпоинты для игры в реальном времени")
@SecurityRequirement(name = "Bearer")
public class GameRoomController {

    private final GameService gameService;

    @GetMapping("/tests")
    @Operation(summary = "Список активных игровых тестов")
    public ResponseEntity<List<GameService.GameTestSummary>> listTests() {
        return ResponseEntity.ok(gameService.listActiveGameTests());
    }

    @PostMapping("/join/{gameTestId}")
    @Operation(
            summary = "Вступить в очередь / найти соперника",
            description = "Если есть ожидающий игрок — создаётся игра и оба получают roomId со статусом MATCHED. " +
                    "Если нет — статус WAITING, клиент подключается к WebSocket и ждёт событие GAME_STARTED."
    )
    public ResponseEntity<JoinGameResponse> join(@PathVariable Long gameTestId,
                                                  @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(gameService.joinGame(gameTestId, user.getId()));
    }

    @DeleteMapping("/queue/{gameTestId}")
    @Operation(summary = "Покинуть очередь ожидания")
    public ResponseEntity<Void> leaveQueue(@PathVariable Long gameTestId,
                                            @AuthenticationPrincipal User user) {
        gameService.leaveQueue(gameTestId, user.getId());
        return ResponseEntity.noContent().build();
    }
}
