package synamyk.dto.game;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class JoinGameResponse {
    /** WAITING or MATCHED */
    private String status;
    private Long roomId;
    private String message;
}
