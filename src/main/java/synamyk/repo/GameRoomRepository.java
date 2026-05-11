package synamyk.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import synamyk.entities.GameRoom;
import synamyk.enums.GameRoomStatus;

import java.util.List;
import java.util.Optional;

public interface GameRoomRepository extends JpaRepository<GameRoom, Long> {
    Optional<GameRoom> findTopByGameTestIdAndStatusOrderByCreatedAtAsc(Long gameTestId, GameRoomStatus status);
    List<GameRoom> findByPlayer1IdOrPlayer2Id(Long player1Id, Long player2Id);
    List<GameRoom> findByGameTestId(Long gameTestId);
    boolean existsByPlayer1IdAndStatus(Long player1Id, GameRoomStatus status);
    boolean existsByPlayer2IdAndStatus(Long player2Id, GameRoomStatus status);
}
