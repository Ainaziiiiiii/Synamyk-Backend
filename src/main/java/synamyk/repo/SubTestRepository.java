package synamyk.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import synamyk.entities.SubTest;

import java.util.List;

@Repository
public interface SubTestRepository extends JpaRepository<SubTest, Long> {
    List<SubTest> findByTestIdAndActiveTrueOrderByLevelOrderAsc(Long testId);
    List<SubTest> findByTestIdOrderByLevelOrderAsc(Long testId);
}