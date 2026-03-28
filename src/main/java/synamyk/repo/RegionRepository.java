package synamyk.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import synamyk.entities.Region;

@Repository
public interface RegionRepository extends JpaRepository<Region, Long> {
}