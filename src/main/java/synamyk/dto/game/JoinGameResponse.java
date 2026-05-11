package synamyk.dto.game;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "Ответ на запрос вступления в игру")
public class JoinGameResponse {

    @Schema(
        description = "Статус поиска. WAITING — соперник ещё не найден, клиент должен подключиться к WebSocket и ждать GAME_STARTED. MATCHED — соперник найден, игра начнётся через ~2 сек.",
        example = "WAITING",
        allowableValues = {"WAITING", "MATCHED"}
    )
    private String status;

    @Schema(
        description = "ID комнаты. Клиент подписывается на /topic/game/{roomId} для получения событий",
        example = "7"
    )
    private Long roomId;

    @Schema(description = "Сообщение для пользователя", example = "Ожидание соперника...")
    private String message;
}
