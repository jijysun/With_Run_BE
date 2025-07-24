package UMC_8th.With_Run.map.service;

import UMC_8th.With_Run.map.dto.*;

import java.util.List;

public interface MapSearchService {
    List<MapResponseDTO.PlaceResponseDto> searchPlacesByKeyword(String keyword);
    List<MapResponseDTO.PlaceResponseDto> searchPlacesByCategory(String category);


    MapResponseDTO.PlaceResponseDto getPlaceDetailByName(String placeName);


    MapResponseDTO.PlaceResponseDto getPlaceDetailByPlaceId(String placeId);

    List<MapResponseDTO.PetFacilityResponseDto> getAllPetFacilities();
    Long createCourse(String accessToken, MapRequestDTO.CourseCreateRequestDto requestDto);
}
