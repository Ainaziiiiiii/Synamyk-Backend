package synamyk.dto;

import lombok.Data;

import java.util.List;

@Data
public class ErrorAnalysisRequest {
    private Long sessionId;
    private List<Long> questionIds; // IDs of wrong questions to analyze
}