package synamyk.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OtpVerifyResponse {
    private Boolean success;
    private String phone;
    private String message;
}