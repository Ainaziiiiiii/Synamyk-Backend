package synamyk.dto.admin;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AdminUserResponse {
    private Long id;
    private String fullName;
    private String phone;
    private String email;
    private String avatarUrl;
    private String regionName;
    private String role;
    private Boolean active;
    private Boolean phoneVerified;
    private LocalDateTime registeredAt;
    private long totalScore;
}
