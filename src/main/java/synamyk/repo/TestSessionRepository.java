package synamyk.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import synamyk.entities.TestSession;

import java.util.List;
import java.util.Optional;

@Repository
public interface TestSessionRepository extends JpaRepository<TestSession, Long> {

    /** Find active (IN_PROGRESS) session for a user + sub-test. */
    Optional<TestSession> findByUserIdAndSubTestIdAndStatus(
            Long userId, Long subTestId, TestSession.SessionStatus status);

    List<TestSession> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<TestSession> findByUserIdAndSubTestIdOrderByCreatedAtDesc(Long userId, Long subTestId);
}