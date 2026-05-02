package synamyk.dto.admin;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AdminVideoListResponse {
    private Long id;
    private String title;
    private String thumbnailUrl;
    private String subject;
    private String duration;
    private Integer viewCount;
    private LocalDateTime createdAt;
    private Boolean active;
    private Long testId;
}
