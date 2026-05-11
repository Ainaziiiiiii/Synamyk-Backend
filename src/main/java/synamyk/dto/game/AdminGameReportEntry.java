package synamyk.dto.game;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AdminGameReportEntry {
    private Long roomId;
    private LocalDateTime playedAt;
    private String player1Name;
    private String player2Name;
    private Integer player1Score;
    private Integer player2Score;
    private String winnerName;
    private Integer totalQuestions;
}
