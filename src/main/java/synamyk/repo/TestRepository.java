package synamyk.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import synamyk.entities.Test;

import java.util.List;

@Repository
public interface TestRepository extends JpaRepository<Test, Long> {
    List<Test> findByActiveTrueOrderByCreatedAtAsc();

    @Query(value = "SELECT t FROM Test t"
            + " WHERE (:search IS NULL OR :search = '' OR LOWER(t.title) LIKE LOWER(CONCAT('%',:search,'%')))"
            + " AND (:subject IS NULL OR :subject = '' OR t.subject = :subject)"
            + " AND (:active IS NULL OR t.active = :active)"
            + " ORDER BY t.createdAt DESC",
            countQuery = "SELECT COUNT(t) FROM Test t"
            + " WHERE (:search IS NULL OR :search = '' OR LOWER(t.title) LIKE LOWER(CONCAT('%',:search,'%')))"
            + " AND (:subject IS NULL OR :subject = '' OR t.subject = :subject)"
            + " AND (:active IS NULL OR t.active = :active)")
    Page<Test> findAllByFilters(
            @Param("search") String search,
            @Param("subject") String subject,
            @Param("active") Boolean active,
            Pageable pageable);

    @Query("SELECT COUNT(q) FROM Question q WHERE q.subTest.test.id = :testId AND q.active = true")
    long countQuestionsByTestId(@Param("testId") Long testId);

    @Query("SELECT COUNT(DISTINCT ts.user.id) FROM TestSession ts WHERE ts.subTest.test.id = :testId AND ts.status = 'COMPLETED'")
    long countAttemptsByTestId(@Param("testId") Long testId);

    @Query("SELECT DISTINCT t.subject FROM Test t WHERE t.subject IS NOT NULL ORDER BY t.subject")
    List<String> findAllSubjects();
}
