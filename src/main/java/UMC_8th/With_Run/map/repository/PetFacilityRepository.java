package UMC_8th.With_Run.map.repository;

import UMC_8th.With_Run.map.entity.PetFacility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PetFacilityRepository extends JpaRepository<PetFacility, Long> {
} 