package synamyk.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ErrorAnalysisResponse {
    private List<QuestionAnalysis> analyses;

    @Data
    @Builder
    public static class QuestionAnalysis {
        private Long questionId;
        private Integer questionIndex; // 1-based
        private String questionText;
        private String wrongAnswer;    // user's wrong answer text
        private String correctAnswer;  // correct answer text
        private String explanation;    // AI-generated explanation
    }
}