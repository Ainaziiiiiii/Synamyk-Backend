package synamyk.dto.game;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class GameAnswerOptionRequest {
    @NotBlank
    private String text;
    private boolean correct;
}
