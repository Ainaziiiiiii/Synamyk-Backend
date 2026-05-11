package synamyk.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;
import synamyk.dto.game.SubmitGameAnswerRequest;
import synamyk.entities.User;
import synamyk.service.GameService;

import java.security.Principal;

@Slf4j
@Controller
@RequiredArgsConstructor
public class GameWebSocketController {

    private final GameService gameService;

    /**
     * Client sends to: /app/game/{roomId}/answer
     * Body: { "optionId": 42 }
     */
    @MessageMapping("/game/{roomId}/answer")
    public void submitAnswer(@DestinationVariable Long roomId,
                             @Payload SubmitGameAnswerRequest request,
                             Principal principal) {
        if (principal == null) {
            log.warn("Unauthenticated answer attempt for room {}", roomId);
            return;
        }
        User user = (User) ((org.springframework.security.authentication.UsernamePasswordAuthenticationToken) principal).getPrincipal();
        gameService.submitAnswer(roomId, user.getId(), request.getOptionId());
    }
}
