package synamyk.dto.admin;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "Request to create or update a question with answer options")
public class CreateQuestionRequest {

    @NotBlank
    @Schema(description = "Question text in Russian", example = "Чему равно 2 + 2?")
    private String text;

    @Schema(description = "Question text in Kyrgyz")
    private String textKy;

    @Schema(description = "Optional section name in Russian (e.g. for ОРТ parts)", example = "1-часть: Математика")
    private String sectionName;

    @Schema(description = "Optional section name in Kyrgyz")
    private String sectionNameKy;

    @Schema(description = "URL of an image attached to the question (optional)")
    private String imageUrl;

    @Schema(description = "Explanation of the correct answer in Russian")
    private String explanation;

    @Schema(description = "Explanation of the correct answer in Kyrgyz")
    private String explanationKy;

    @Schema(description = "Display order within the sub-test (0-based)", example = "0")
    private Integer orderIndex = 0;

    @Schema(description = "Points awarded for a correct answer", example = "1")
    private Integer pointValue = 1;

    @NotNull
    @Size(min = 2, max = 6)
    @Schema(description = "Answer options (2–6). At least one must have isCorrect = true. Multiple correct options are allowed.")
    private List<AnswerOptionRequest> options;

    @Data
    @Schema(description = "A single answer option")
    public static class AnswerOptionRequest {

        @NotBlank
        @Schema(description = "Option label: А, Б, В, Г, Д", example = "А")
        private String label;

        @NotBlank
        @Schema(description = "Option text in Russian", example = "4")
        private String text;

        @Schema(description = "Option text in Kyrgyz")
        private String textKy;

        @Schema(description = "Whether this option is the correct answer", example = "false")
        private Boolean isCorrect = false;

        @Schema(description = "Display order of this option", example = "0")
        private Integer orderIndex = 0;
    }
}