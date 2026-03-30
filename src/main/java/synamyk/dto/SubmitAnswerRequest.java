package synamyk.dto;

import lombok.Data;

import java.util.List;

@Data
public class SubmitAnswerRequest {
    private Long questionId;
    /** null or empty means skip */
    private List<Long> selectedOptionIds;
}