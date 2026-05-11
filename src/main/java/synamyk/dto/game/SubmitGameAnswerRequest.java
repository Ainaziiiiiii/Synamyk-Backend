package synamyk.dto.game;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Сообщение ответа на вопрос (отправляется через WebSocket STOMP)")
public class SubmitGameAnswerRequest {

    @Schema(
        description = "ID выбранного варианта ответа. Берётся из поля options[].id в событии NEXT_QUESTION",
        example = "103"
    )
    private Long optionId;
}
