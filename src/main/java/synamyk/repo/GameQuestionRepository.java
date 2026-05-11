package synamyk.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import synamyk.entities.GameQuestion;

import java.util.List;

public interface GameQuestionRepository extends JpaRepository<GameQuestion, Long> {
    List<GameQuestion> findByGameTestIdAndActiveTrue(Long gameTestId);
    List<GameQuestion> findByGameTestId(Long gameTestId);
    long countByGameTestIdAndActiveTrue(Long gameTestId);
}
