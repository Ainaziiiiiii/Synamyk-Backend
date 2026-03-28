package synamyk.dto.admin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class CreateQuestionRequest {
    @NotBlank
    private String text;
    private String sectionName;
    private String imageUrl;
    private String explanation;
    private Integer orderIndex = 0;
    private Integer pointValue = 1;

    @NotNull
    @Size(min = 2, max = 6)
    private List<AnswerOptionRequest> options;

    @Data
    public static class AnswerOptionRequest {
        @NotBlank
        private String label;
        @NotBlank
        private String text;
        private Boolean isCorrect = false;
        private Integer orderIndex = 0;
    }
}