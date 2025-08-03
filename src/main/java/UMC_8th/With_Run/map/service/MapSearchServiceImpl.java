package UMC_8th.With_Run.map.service;

import UMC_8th.With_Run.common.apiResponse.status.ErrorCode;
import UMC_8th.With_Run.common.exception.handler.MapHandler;
import UMC_8th.With_Run.map.dto.MapRequestDTO;
import UMC_8th.With_Run.map.dto.MapResponseDTO;
import UMC_8th.With_Run.map.entity.PetFacility;
import UMC_8th.With_Run.map.repository.PetFacilityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MapSearchServiceImpl implements MapSearchService {

    private final PetFacilityRepository petFacilityRepository;
    private final CourseService courseService;

    @Override
    public List<PetFacility> searchPlacesByCategory(String category) {
        return petFacilityRepository.findByCategoryContainingIgnoreCase(category);
    }

    @Override
    public Page<MapResponseDTO.PetFacilityResponseDto> searchPlacesByCategory(String category, int page, int size) {
        validatePagingParameters(page, size);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<PetFacility> facilities = petFacilityRepository.findByCategoryContainingIgnoreCase(category, pageable);

        return facilities.map(facility -> MapResponseDTO.PetFacilityResponseDto.builder()
                .id(facility.getId())
                .name(facility.getName())
                .category(facility.getCategory())
                .longitude(facility.getLongitude())
                .latitude(facility.getLatitude())
                .address(facility.getAddress())
                .closed_day(facility.getClosed_day())
                .running_time(facility.getRunning_time())
                .has_parking(facility.getHas_parking())
                .build());
    }

    @Override
    public MapResponseDTO.PetFacilityPageResponseDto searchPlacesByCategorySimple(String category, int page, int size) {
        validatePagingParameters(page, size);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<PetFacility> facilities = petFacilityRepository.findByCategoryContainingIgnoreCase(category, pageable);

        // 페이지 범위 검증
        if (page >= facilities.getTotalPages() && facilities.getTotalPages() > 0) {
            throw new MapHandler(ErrorCode.PAGE_OUT_OF_RANGE);
        }

        List<MapResponseDTO.PetFacilityResponseDto> content = facilities.getContent().stream()
                .map(facility -> MapResponseDTO.PetFacilityResponseDto.builder()
                        .id(facility.getId())
                        .name(facility.getName())
                        .category(facility.getCategory())
                        .longitude(facility.getLongitude())
                        .latitude(facility.getLatitude())
                        .address(facility.getAddress())
                        .closed_day(facility.getClosed_day())
                        .running_time(facility.getRunning_time())
                        .has_parking(facility.getHas_parking())
                        .build())
                .collect(Collectors.toList());

        return MapResponseDTO.PetFacilityPageResponseDto.builder()
                .content(content)
                .totalElements(facilities.getTotalElements())
                .totalPages(facilities.getTotalPages())
                .size(facilities.getSize())
                .number(facilities.getNumber())
                .first(facilities.isFirst())
                .last(facilities.isLast())
                .build();
    }

    private void validatePagingParameters(int page, int size) {
        // 페이지 번호 검증
        if (page < 0) {
            throw new MapHandler(ErrorCode.WRONG_PAGE);
        }
        
        // 페이지 크기 검증
        if (size < 1 || size > 100) {
            throw new MapHandler(ErrorCode.WRONG_PAGE_SIZE);
        }
    }

    @Override
    public MapResponseDTO.PetFacilityResponseDto getPetFacilityById(Long id) {
        PetFacility petFacility = petFacilityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pet facility not found with id: " + id));

        return MapResponseDTO.PetFacilityResponseDto.builder()
                .id(petFacility.getId())
                .name(petFacility.getName())
                .category(petFacility.getCategory())
                .longitude(petFacility.getLongitude())
                .latitude(petFacility.getLatitude())
                .address(petFacility.getAddress())
                .closed_day(petFacility.getClosed_day())
                .running_time(petFacility.getRunning_time())
                .has_parking(petFacility.getHas_parking())
                .build();
    }

    @Override
    public Long createCourse(MapRequestDTO.CourseCreateRequestDto requestDto) {
        return courseService.createCourse(requestDto.getUserId(), requestDto);
    }
}