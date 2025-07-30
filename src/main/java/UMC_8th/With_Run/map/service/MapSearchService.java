package UMC_8th.With_Run.map.service;

import UMC_8th.With_Run.map.dto.MapRequestDTO;
import UMC_8th.With_Run.map.dto.MapResponseDTO;

import java.util.List;

public interface MapSearchService {
    List<MapResponseDTO.PlaceResponseDto> searchPlacesByKeyword(String keyword);
    List<MapResponseDTO.PlaceResponseDto> searchPlacesByCategory(String category);


    MapResponseDTO.PlaceResponseDto getPlaceDetailByName(String placeName);


    MapResponseDTO.PlaceResponseDto getPlaceDetailByPlaceId(String placeId);

    MapResponseDTO.PetFacilityResponseDto getPetFacilityById(Long id);

    // 수정 부분 시작: createCourse 메소드 시그니처 변경
    // Long createCourse(String accessToken, MapRequestDTO.CourseCreateRequestDto requestDto); // 이전
    Long createCourse(MapRequestDTO.CourseCreateRequestDto requestDto); // <--- 이렇게 변경
    // 수정 부분 끝
}