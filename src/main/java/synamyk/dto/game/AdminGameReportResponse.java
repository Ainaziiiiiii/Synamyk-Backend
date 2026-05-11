package synamyk.dto.game;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class AdminGameReportResponse {
    private Long gameTestId;
    private String gameTestTitle;
    private long totalGames;
    private long totalPlayers;
    private List<AdminGameReportEntry> games;
}
