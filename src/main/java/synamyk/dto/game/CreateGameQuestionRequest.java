package synamyk.dto.game;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "Запрос на создание вопроса для игрового теста")
public class CreateGameQuestionRequest {

    @NotBlank
    @Schema(description = "Текст вопроса", example = "Чему равно среднее арифметическое чисел 10, 20 и 30?")
    private String text;

    @Schema(description = "URL изображения к вопросу (необязательно)", example = "https://cdn.example.com/img/q1.png")
    private String imageUrl;

    @Schema(description = "Порядковый номер вопроса (необязательно, по умолчанию — порядок добавления)", example = "0")
    private Integer orderIndex;

    @NotEmpty
    @Size(min = 2, max = 6)
    @Valid
    @Schema(description = "Варианты ответа (от 2 до 6 штук). Ровно один должен иметь correct=true")
    private List<GameAnswerOptionRequest> options;
}
