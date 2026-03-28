package synamyk.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class OtpSendResponse {
    private Boolean success;
    private String phone;
    private String transactionId;
    private String token;
    private LocalDateTime expiresAt;
    private String message;
}