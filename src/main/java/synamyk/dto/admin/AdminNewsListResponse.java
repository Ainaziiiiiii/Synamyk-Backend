package synamyk.dto.admin;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AdminNewsListResponse {
    private Long id;
    private String title;
    private String coverImageUrl;
    private String type;
    private Integer viewCount;
    private String authorName;
    private LocalDateTime publishedAt;
    private Boolean active;
}
