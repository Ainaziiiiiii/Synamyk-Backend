package synamyk.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import synamyk.enums.MediaFileType;
import synamyk.repo.UserRepository;
import synamyk.service.MinioService;

@RestController
@RequestMapping("/api/upload")
@RequiredArgsConstructor
@Tag(name = "Загрузка файлов", description = "Загрузка изображений в MinIO. Возвращает публичный URL для сохранения в полях avatarUrl / coverImageUrl / thumbnailUrl.")
@SecurityRequirement(name = "Bearer")
public class UploadController {

    private final MinioService minioService;
    private final UserRepository userRepository;

    /**
     * POST /api/upload
     * Uploads an image and returns its permanent public URL.
     *
     * type values:
     *   AVATAR           — profile avatar (use userId as entityId)
     *   NEWS_COVER       — news article cover (use newsId or "new")
     *   VIDEO_THUMBNAIL  — video lesson thumbnail (use videoId or "new")
     *
     * The returned URL is stored directly in avatarUrl / coverImageUrl / thumbnailUrl fields.
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "Загрузить изображение",
            description = "Загружает изображение (до 10 МБ, только image/*) в MinIO и возвращает постоянный публичный URL.\n\n" +
                    "**Типы и пути хранения:**\n\n" +
                    "| type | Куда сохранять URL |\n" +
                    "|------|-------------------|\n" +
                    "| `AVATAR` | `PUT /api/profile` → `avatarUrl` |\n" +
                    "| `NEWS_COVER` | `POST/PUT /api/admin/news` → `coverImageUrl` |\n" +
                    "| `VIDEO_THUMBNAIL` | `POST/PUT /api/admin/videos` → `thumbnailUrl` |\n\n" +
                    "Для `AVATAR` поле `entityId` подставляется автоматически."
    )
    public ResponseEntity<UploadResponse> upload(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestPart("file") MultipartFile file,
            @Parameter(description = "Тип файла: AVATAR, NEWS_COVER, VIDEO_THUMBNAIL") @RequestParam MediaFileType type,
            @Parameter(description = "ID сущности (для AVATAR не нужен — берётся автоматически)") @RequestParam(defaultValue = "general") String entityId) {

        // For AVATAR, default entityId to the current user's id
        if (type == MediaFileType.AVATAR) {
            Long userId = userRepository.findByPhone(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found")).getId();
            entityId = String.valueOf(userId);
        }

        String url = minioService.upload(file, type, entityId);
        return ResponseEntity.ok(UploadResponse.builder().url(url).build());
    }

    @Data
    @Builder
    static class UploadResponse {
        /** Permanent public URL — store this directly in avatarUrl / coverImageUrl / thumbnailUrl. */
        private String url;
    }
}