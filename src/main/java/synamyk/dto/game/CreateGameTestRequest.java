package synamyk.dto.game;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
public class CreateGameTestRequest {
    @NotBlank
    private String title;
    private String description;

    @Min(5)
    private Integer timeLimitSeconds = 30;

    /** 0 means use all available questions. */
    @Min(0)
    private Integer questionsPerGame = 0;

    @Valid
    private List<CreateGameQuestionRequest> questions;
}
