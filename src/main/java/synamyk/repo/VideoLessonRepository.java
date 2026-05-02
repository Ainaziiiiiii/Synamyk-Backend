package synamyk.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import synamyk.entities.VideoLesson;

import java.util.List;

@Repository
public interface VideoLessonRepository extends JpaRepository<VideoLesson, Long> {

    List<VideoLesson> findByActiveTrueOrderByOrderIndexAsc();

    List<VideoLesson> findByTestIdAndActiveTrueOrderByOrderIndexAsc(Long testId);

    @Query(value = "SELECT v FROM VideoLesson v LEFT JOIN v.test t"
            + " WHERE (:search IS NULL OR :search = '' OR LOWER(v.title) LIKE LOWER(CONCAT('%',:search,'%')))"
            + " AND (:testId IS NULL OR t.id = :testId)"
            + " AND (:active IS NULL OR v.active = :active)"
            + " ORDER BY v.orderIndex ASC",
            countQuery = "SELECT COUNT(v) FROM VideoLesson v LEFT JOIN v.test t"
            + " WHERE (:search IS NULL OR :search = '' OR LOWER(v.title) LIKE LOWER(CONCAT('%',:search,'%')))"
            + " AND (:testId IS NULL OR t.id = :testId)"
            + " AND (:active IS NULL OR v.active = :active)")
    Page<VideoLesson> findAllByFilters(
            @Param("search") String search,
            @Param("testId") Long testId,
            @Param("active") Boolean active,
            Pageable pageable);
}
