package synamyk.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import synamyk.entities.GameAnswerOption;

public interface GameAnswerOptionRepository extends JpaRepository<GameAnswerOption, Long> {
}
