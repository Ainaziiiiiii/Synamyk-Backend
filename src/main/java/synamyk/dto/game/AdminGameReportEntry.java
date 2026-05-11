package synamyk.dto.game;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@Schema(description = "Запись об одной сыгранной партии")
public class AdminGameReportEntry {

    @Schema(description = "ID комнаты", example = "7")
    private Long roomId;

    @Schema(description = "Дата и время окончания партии", example = "2026-05-11T14:30:00")
    private LocalDateTime playedAt;

    @Schema(description = "Имя первого игрока", example = "Айнур Токтоматова")
    private String player1Name;

    @Schema(description = "Имя второго игрока", example = "Бекзат")
    private String player2Name;

    @Schema(description = "Количество правильных ответов первого игрока", example = "7")
    private Integer player1Score;

    @Schema(description = "Количество правильных ответов второго игрока", example = "5")
    private Integer player2Score;

    @Schema(description = "Имя победителя (или «Ничья»)", example = "Айнур Токтоматова")
    private String winnerName;

    @Schema(description = "Всего вопросов в партии", example = "10")
    private Integer totalQuestions;
}
