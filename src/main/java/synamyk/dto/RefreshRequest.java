package synamyk.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Запрос на обновление access-токена")
public class RefreshRequest {

    @NotBlank
    @Schema(description = "Refresh-токен (UUID), полученный при входе или регистрации", example = "550e8400-e29b-41d4-a716-446655440000")
    private String refreshToken;
}