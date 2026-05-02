package synamyk.dto.admin;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class AdminTestListResponse {
    private Long id;
    private String title;
    private String iconUrl;
    private String subject;
    private BigDecimal price;
    private long questionCount;
    private long attemptsCount;
    private LocalDateTime createdAt;
    private Boolean active;
}
