package synamyk.dto.admin;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Request to create or update a video lesson")
public class CreateVideoLessonRequest {

    @NotBlank
    @Schema(description = "Lesson title in Russian", example = "Логарифмы и степени")
    private String title;

    @Schema(description = "Lesson title in Kyrgyz")
    private String titleKy;

    @Schema(description = "Short description in Russian")
    private String description;

    @Schema(description = "Short description in Kyrgyz")
    private String descriptionKy;

    @Schema(description = "URL of the thumbnail image (upload via POST /api/upload)")
    private String thumbnailUrl;

    @NotBlank
    @Schema(description = "YouTube video URL", example = "https://youtu.be/abc123")
    private String videoUrl;

    @Schema(description = "Optional: associate lesson with a specific test ID")
    private Long testId;

    @Schema(description = "Display order (lower = first)", example = "0")
    private Integer orderIndex = 0;
}