package synamyk.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import synamyk.entities.Question;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findBySubTestIdAndActiveTrueOrderByOrderIndexAsc(Long subTestId);
    List<Question> findBySubTestIdOrderByOrderIndexAsc(Long subTestId);
    long countBySubTestIdAndActiveTrue(Long subTestId);
}