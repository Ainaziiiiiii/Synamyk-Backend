package synamyk.dto.game;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GameEvent {

    private String type;
    private Long roomId;

    // GAME_STARTED
    private Long player1Id;
    private Long player2Id;
    private String player1Name;
    private String player2Name;
    private String player1Avatar;
    private String player2Avatar;

    // NEXT_QUESTION
    private Integer questionIndex;
    private Integer totalQuestions;
    private Integer timeLimitSeconds;
    private QuestionPayload question;

    // Scores (sent with NEXT_QUESTION, ANSWER_RESULT, GAME_OVER)
    private Integer player1Score;
    private Integer player2Score;

    // ANSWER_RESULT
    private Boolean correct;

    // GAME_OVER
    private Long winnerId;

    // Generic
    private String message;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class QuestionPayload {
        private Long id;
        private String text;
        private String imageUrl;
        private List<OptionPayload> options;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class OptionPayload {
        private Long id;
        private String text;
    }
}
