package synamyk.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import synamyk.entities.OTPCode;

@Data
public class VerifyOtpRequest {

    @NotBlank(message = "Phone is required")
    private String phone;

    @NotBlank(message = "Code is required")
    private String code;

    @NotNull(message = "OTP type is required")
    private OTPCode.OtpType type;
}