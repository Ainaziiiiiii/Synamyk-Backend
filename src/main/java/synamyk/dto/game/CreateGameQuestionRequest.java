package synamyk.dto.game;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class CreateGameQuestionRequest {
    @NotBlank
    private String text;
    private String imageUrl;
    private Integer orderIndex;

    @NotEmpty
    @Size(min = 2, max = 6)
    @Valid
    private List<GameAnswerOptionRequest> options;
}
