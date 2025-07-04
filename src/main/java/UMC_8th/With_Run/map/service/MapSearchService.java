package UMC_8th.With_Run.map.service;

import UMC_8th.With_Run.map.dto.CourseCreateRequestDto;
import UMC_8th.With_Run.map.dto.PetFacilityResponseDto;
import UMC_8th.With_Run.map.dto.PlaceResponseDto;

import java.util.List;

public interface MapSearchService {
    List<PlaceResponseDto> searchPlacesByKeyword(String keyword);
    List<PlaceResponseDto> searchPlacesByCategory(String category);
    PlaceResponseDto getPlaceById(Long placeId);
    List<PetFacilityResponseDto> getAllPetFacilities();
    //Long createCourse(String accessToken, CourseCreateRequestDto requestDto);
}

