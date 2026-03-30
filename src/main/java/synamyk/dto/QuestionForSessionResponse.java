package synamyk.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class QuestionForSessionResponse {
    private Long questionId;
    private Integer index;          // 0-based
    private Integer totalQuestions;
    private String sectionName;
    private String text;
    private String imageUrl;
    private Integer pointValue;
    private List<AnswerOptionResponse> options;
    private Long remainingSeconds;
    private List<Long> selectedOptionIds;  // empty if not answered yet
    private Boolean isSkipped;
}