package synamyk.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import synamyk.entities.UserAnswer;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserAnswerRepository extends JpaRepository<UserAnswer, Long> {

    @Query("SELECT ua FROM UserAnswer ua JOIN ua.question q WHERE ua.session.id = :sessionId ORDER BY q.orderIndex ASC")
    List<UserAnswer> findBySessionIdOrderByQuestionOrderIndex(@Param("sessionId") Long sessionId);

    Optional<UserAnswer> findBySessionIdAndQuestionId(Long sessionId, Long questionId);

    long countBySessionIdAndIsCorrectTrue(Long sessionId);
}