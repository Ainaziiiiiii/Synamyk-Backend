package synamyk.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreatePaymentRequest {
    @NotNull
    private Long testId;
    private String redirectUrl;
}