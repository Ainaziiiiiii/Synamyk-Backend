package synamyk.dto;

import lombok.Data;

@Data
public class SubmitAnswerRequest {
    private Long questionId;
    private Long selectedOptionId; // null means skip
}