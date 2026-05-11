package synamyk.dto.game;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "Запрос на создание игрового теста")
public class CreateGameTestRequest {

    @NotBlank
    @Schema(description = "Название теста", example = "Математика: Арифметика")
    private String title;

    @Schema(description = "Описание теста (необязательно)", example = "Вопросы по базовой арифметике для 9-го класса")
    private String description;

    @Min(5)
    @Schema(
        description = "Лимит времени на один вопрос в секундах (минимум 5). По умолчанию 30",
        example = "30",
        minimum = "5"
    )
    private Integer timeLimitSeconds = 30;

    @Min(0)
    @Schema(
        description = "Количество вопросов за одну партию (0 = все вопросы теста в случайном порядке)",
        example = "10",
        minimum = "0"
    )
    private Integer questionsPerGame = 0;

    @Valid
    @Schema(description = "Список вопросов (необязательно — можно добавить позже через POST /{id}/questions)")
    private List<CreateGameQuestionRequest> questions;
}
