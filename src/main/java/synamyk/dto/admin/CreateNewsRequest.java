package synamyk.dto.admin;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "Request to create or update a news article")
public class CreateNewsRequest {

    @NotBlank
    @Schema(description = "Article title in Russian", example = "Новые функции приложения")
    private String title;

    @Schema(description = "Article title in Kyrgyz")
    private String titleKy;

    @Schema(description = "Cover image URL")
    private String coverImageUrl;

    @NotBlank
    @Schema(description = "Full article content in Russian (Markdown or plain text)")
    private String content;

    @Schema(description = "Full article content in Kyrgyz")
    private String contentKy;

    @Schema(description = "Type: NEWS, ARTICLE, ANNOUNCEMENT", example = "NEWS")
    private String type;

    @NotNull
    @Schema(description = "Publication date-time", example = "2026-03-30T12:00:00")
    private LocalDateTime publishedAt;
}