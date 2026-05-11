package synamyk.dto.game;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@Schema(description = "Отчёт по игровому тесту для администратора")
public class AdminGameReportResponse {

    @Schema(description = "ID игрового теста", example = "1")
    private Long gameTestId;

    @Schema(description = "Название игрового теста", example = "Математика: Арифметика")
    private String gameTestTitle;

    @Schema(description = "Всего завершённых партий", example = "42")
    private long totalGames;

    @Schema(description = "Всего уникальных игроков", example = "18")
    private long totalPlayers;

    @Schema(description = "Список всех завершённых партий, отсортированных по дате")
    private List<AdminGameReportEntry> games;
}
