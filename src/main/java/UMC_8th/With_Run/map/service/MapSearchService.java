package UMC_8th.With_Run.map.service;

import UMC_8th.With_Run.map.dto.MapRequestDTO;
import UMC_8th.With_Run.map.dto.MapResponseDTO;
import UMC_8th.With_Run.map.entity.PetFacility;
import org.springframework.data.domain.Page;

import java.util.List;

public interface MapSearchService {
    List<PetFacility> searchPlacesByCategory(String category);
    Page<MapResponseDTO.PetFacilityResponseDto> searchPlacesByCategory(String category, int page, int size);
    MapResponseDTO.PetFacilityPageResponseDto searchPlacesByCategorySimple(String category, int page, int size);
    MapResponseDTO.PetFacilityResponseDto getPetFacilityById(Long id);
    Long createCourse(MapRequestDTO.CourseCreateRequestDto requestDto);
}
