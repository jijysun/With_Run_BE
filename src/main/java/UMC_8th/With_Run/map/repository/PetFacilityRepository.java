package UMC_8th.With_Run.map.repository;

import UMC_8th.With_Run.map.entity.PetFacility;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PetFacilityRepository extends JpaRepository<PetFacility, Long> {
    List<PetFacility> findByCategoryContainingIgnoreCase(String category);
    Page<PetFacility> findByCategoryContainingIgnoreCase(String category, Pageable pageable);
}