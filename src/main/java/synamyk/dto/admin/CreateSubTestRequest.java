package synamyk.dto.admin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateSubTestRequest {
    @NotBlank
    private String title;
    @NotBlank
    private String levelName;
    @NotNull
    private Integer levelOrder;
    private Boolean isPaid = false;
    @NotNull
    private Integer durationMinutes;
}