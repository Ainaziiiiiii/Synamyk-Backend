package synamyk.dto.admin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateTestRequest {
    @NotBlank
    private String title;
    private String description;
    private String iconUrl;
    @NotNull
    private BigDecimal price;
}