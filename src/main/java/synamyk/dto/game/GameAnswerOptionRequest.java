package synamyk.dto.game;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Вариант ответа для игрового вопроса")
public class GameAnswerOptionRequest {

    @NotBlank
    @Schema(description = "Текст варианта ответа", example = "17")
    private String text;

    @Schema(description = "Является ли вариант правильным ответом. Ровно один вариант должен быть true", example = "true")
    private boolean correct;
}
