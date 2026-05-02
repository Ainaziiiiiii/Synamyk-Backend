package synamyk.dto.admin;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdminRatingEntryResponse {
    private Long userId;
    private String fullName;
    private String phone;
    private String avatarUrl;
    private int rank;
    private long totalPoints;
    private long pvpWins;
}
