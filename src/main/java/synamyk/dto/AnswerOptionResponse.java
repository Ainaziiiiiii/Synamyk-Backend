package synamyk.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AnswerOptionResponse {
    private Long id;
    private String label;
    private String text;
    private Integer orderIndex;
}