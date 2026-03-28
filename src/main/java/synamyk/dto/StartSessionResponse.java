package synamyk.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class StartSessionResponse {
    private Long sessionId;
    private Long subTestId;
    private String subTestTitle;
    private String levelName;
    private Integer totalQuestions;
    private Integer durationMinutes;
    private LocalDateTime expiresAt;
    private Long remainingSeconds;
    private Integer currentIndex;
    private Boolean isResumed;
}