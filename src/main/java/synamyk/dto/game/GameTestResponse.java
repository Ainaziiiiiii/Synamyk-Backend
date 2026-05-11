package synamyk.dto.game;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class GameTestResponse {
    private Long id;
    private String title;
    private String description;
    private Integer timeLimitSeconds;
    private Integer questionsPerGame;
    private Boolean active;
    private long questionCount;
    private List<QuestionDetail> questions;

    @Data
    @Builder
    public static class QuestionDetail {
        private Long id;
        private String text;
        private String imageUrl;
        private Integer orderIndex;
        private Boolean active;
        private List<OptionDetail> options;
    }

    @Data
    @Builder
    public static class OptionDetail {
        private Long id;
        private String text;
        private Boolean correct;
        private Integer orderIndex;
    }
}
