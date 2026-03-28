package synamyk.dto.admin;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class AdminTestResponse {
    private Long id;
    private String title;
    private String description;
    private String iconUrl;
    private BigDecimal price;
    private Boolean active;
    private List<AdminSubTestResponse> subTests;

    @Data
    @Builder
    public static class AdminSubTestResponse {
        private Long id;
        private String title;
        private String levelName;
        private Integer levelOrder;
        private Boolean isPaid;
        private Integer durationMinutes;
        private Long questionCount;
        private Boolean active;
    }
}