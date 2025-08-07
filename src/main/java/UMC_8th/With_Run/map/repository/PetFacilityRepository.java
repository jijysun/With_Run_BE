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


    Page<PetFacility> findByAddressContaining(String address, Pageable pageable);

    // 수정: town 컬럼을 기준으로 검색하는 메소드 추가
    // 주석: town 컬럼에 특정 문자열이 포함된 시설을 조회
    Page<PetFacility> findByTownContaining(String town, Pageable pageable);

    // 수정: 카테고리와 town 컬럼을 모두 포함하는 시설을 찾는 메소드 추가
    Page<PetFacility> findByCategoryContainingIgnoreCaseAndTownContaining(String category, String town, Pageable pageable);
    Page<PetFacility> findByCategoryContainingIgnoreCaseAndAddressContaining(String category, String address, Pageable pageable);





}