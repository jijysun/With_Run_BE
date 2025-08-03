package UMC_8th.With_Run.map.service;

import UMC_8th.With_Run.common.apiResponse.status.ErrorCode;
import UMC_8th.With_Run.common.exception.handler.MapHandler;
import UMC_8th.With_Run.map.dto.MapRequestDTO;
import UMC_8th.With_Run.map.dto.MapResponseDTO;
import UMC_8th.With_Run.map.entity.PetFacility;
import UMC_8th.With_Run.map.entity.Pin;
import UMC_8th.With_Run.map.repository.PetFacilityRepository;
import UMC_8th.With_Run.map.repository.PinRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MapServiceImpl implements MapService {

    private final PetFacilityRepository petFacilityRepository;
    private final PinRepository pinRepository;
    private final CourseService courseService;

    @Override
    public MapResponseDTO.PetFacilityPageResponseDto getPetFacilityByCategory(String category, int page, int size) {
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

    @Override
    @Transactional
    public Long createPin(MapRequestDTO.PinRequestDto requestDto) {
        Pin pin = Pin.builder()
                .name(requestDto.getName())
                .detail(requestDto.getDetail())
                .color(requestDto.getColor())
                .latitude(requestDto.getLatitude())
                .longitude(requestDto.getLongitude())
                .createdAt(LocalDateTime.now())
                .build();

        pinRepository.save(pin);

        return pin.getId();
    }

    @Override
    @Transactional
    public Long updatePin(Long pinId, MapRequestDTO.PinRequestDto requestDto) {
        Pin pin = pinRepository.findById(pinId)
                .orElseThrow(() -> new IllegalArgumentException("해당 핀 없음"));
        pin.setName(requestDto.getName());
        pin.setDetail(requestDto.getDetail());
        pin.setColor(requestDto.getColor());
        pin.setLatitude(requestDto.getLatitude());
        pin.setLongitude(requestDto.getLongitude());
        pin.setUpdatedAt(LocalDateTime.now());

        pinRepository.save(pin);
        return pin.getId();
    }

    @Override
    @Transactional
    public Long deletePin(Long pinId) {
        Pin pin = pinRepository.findById(pinId)
                .orElseThrow(() -> new IllegalArgumentException("해당 핀 없음"));

        pinRepository.deleteById(pinId); // 물리적 삭제

        return pin.getId(); // 삭제된 핀의 ID를 반환
    }


    @Override
    @Transactional(readOnly = true) // 데이터 조회만 하므로 readOnly=true 설정
    public MapResponseDTO.GetPinDto getPinById(Long pinId) {
        Pin pin = pinRepository.findById(pinId)
                .orElseThrow(() -> new IllegalArgumentException("해당 핀 없음"));
        return fromEntity(pin);
    }

    public static MapResponseDTO.GetPinDto fromEntity(Pin pin) {
        return MapResponseDTO.GetPinDto.builder()
                .pinId(pin.getId())
                .courseId(pin.getCourseId())
                .name(pin.getName())
                .detail(pin.getDetail())
                .color(pin.getColor())
                .latitude(pin.getLatitude())
                .longitude(pin.getLongitude())
                .build();
    }
}