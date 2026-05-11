package synamyk.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import synamyk.entities.GameTest;

import java.util.List;

public interface GameTestRepository extends JpaRepository<GameTest, Long> {
    List<GameTest> findByActiveTrue();
}
