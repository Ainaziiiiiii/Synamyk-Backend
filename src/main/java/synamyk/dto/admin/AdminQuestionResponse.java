package synamyk.dto.admin;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class AdminQuestionResponse {
    private Long id;
    private String sectionName;
    private String text;
    private String imageUrl;
    private String explanation;
    private Integer orderIndex;
    private Integer pointValue;
    private Boolean active;
    private List<OptionResponse> options;

    @Data
    @Builder
    public static class OptionResponse {
        private Long id;
        private String label;
        private String text;
        private Boolean isCorrect;
        private Integer orderIndex;
    }
}