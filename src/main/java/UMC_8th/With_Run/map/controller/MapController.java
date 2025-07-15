package UMC_8th.With_Run.map.controller;

import UMC_8th.With_Run.common.apiResponse.StndResponse;
import UMC_8th.With_Run.common.apiResponse.status.SuccessCode;
import UMC_8th.With_Run.map.dto.*;
import UMC_8th.With_Run.map.service.MapSearchService;
import UMC_8th.With_Run.map.service.PinService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "지도 API", description = "Swagger 테스트용 지도 관련 API")
@RestController
@RequestMapping("/api/maps")
@RequiredArgsConstructor
public class MapController {

    private final MapSearchService mapSearchService;

    private final PinService pinService;

    @Operation(summary = "키워드 검색", description = "키워드로 장소를 검색합니다.")
    @GetMapping("/search/keyword")
    public StndResponse<List<MapResponseDTO.PlaceResponseDto>> searchByKeyword(
            @Parameter(description = "검색 키워드", required = true)
            @RequestParam String query) {

        List<MapResponseDTO.PlaceResponseDto> result = mapSearchService.searchPlacesByKeyword(query);
        return StndResponse.onSuccess(result, SuccessCode.INQUIRY_SUCCESS);
    }

    @Operation(summary = "카테고리 검색", description = "카테고리로 장소를 검색합니다.")
    @GetMapping("/search/categories")
    public StndResponse<List<MapResponseDTO.PlaceResponseDto>> searchByCategory(
            @Parameter(description = "카테고리 타입 (예: 약국, 병원)", required = true)
            @RequestParam String type) {

        List<MapResponseDTO.PlaceResponseDto> result = mapSearchService.searchPlacesByCategory(type);
        return StndResponse.onSuccess(result, SuccessCode.INQUIRY_SUCCESS);
    }

    @Operation(summary = "특정 시설 클릭 (상세 정보)", description = "선택한 장소의 상세 정보를 반환합니다.")
    @GetMapping("/places/{placeId}")
    public StndResponse<MapResponseDTO.PlaceResponseDto> getPlaceDetail(
            @Parameter(description = "장소 ID", required = true)
            @PathVariable Long placeId) {

        MapResponseDTO.PlaceResponseDto place = mapSearchService.getPlaceById(placeId);
        return StndResponse.onSuccess(place, SuccessCode.INQUIRY_SUCCESS);
    }

    @Operation(summary = "핀 생성")
    @PostMapping("/pins")
    public StndResponse<MapResponseDTO.PinResponseDto> createPin(@RequestBody MapRequestDTO.PinRequestDto requestDto) {
        MapResponseDTO.PinResponseDto pin = pinService.createPin(requestDto);
        return StndResponse.onSuccess(pin, SuccessCode.CREATE_SUCCESS);
    }

    @Operation(summary = "핀 수정")
    @PatchMapping("/pins/{pinId}")
    public StndResponse<MapResponseDTO.PinResponseDto> updatePin(@PathVariable Long pinId,
                                                                 @RequestBody MapRequestDTO.PinRequestDto requestDto) {
        MapResponseDTO.PinResponseDto updated = pinService.updatePin(pinId, requestDto);
        return StndResponse.onSuccess(updated, SuccessCode.UPDATE_SUCCESS);
    }

    @Operation(summary = "핀 삭제")
    @DeleteMapping("/pins/{pinId}")
    public StndResponse<MapResponseDTO.PinResponseDto> deletePin(@PathVariable Long pinId) {
        MapResponseDTO.PinResponseDto deleted = pinService.deletePin(pinId);
        return StndResponse.onSuccess(deleted, SuccessCode.DELETE_SUCCESS);
    }

    @Operation(summary = "산책 코스 생성", description = "산책 코스를 등록합니다.")
    @PostMapping("/courses")
    public StndResponse<MapResponseDTO.CourseCreateResponseDto> createCourse(
            @RequestHeader("Authorization") String accessToken,
            @RequestBody @Valid MapRequestDTO.CourseCreateRequestDto requestDto) {

        Long courseId = mapSearchService.createCourse(accessToken, requestDto);

        MapResponseDTO.CourseCreateResponseDto response = MapResponseDTO.CourseCreateResponseDto.builder()
                .courseId(courseId)
                .message("산책 코스가 성공적으로 등록되었어요!")
                .build();

        return StndResponse.onSuccess(response, SuccessCode.REQUEST_SUCCESS);
    }



    @Operation(summary = "반려동물 시설 전체 조회", description = "모든 반려동물 시설 목록을 조회합니다.")
    @GetMapping("/pet-facilities")
    public StndResponse<List<MapResponseDTO.PetFacilityResponseDto>> getAllPetFacilities() {
        List<MapResponseDTO.PetFacilityResponseDto> facilities = mapSearchService.getAllPetFacilities();
        return StndResponse.onSuccess(facilities, SuccessCode.INQUIRY_SUCCESS);
    }
}
