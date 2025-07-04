package UMC_8th.With_Run.map.controller;

import UMC_8th.With_Run.map.dto.CourseCreateRequestDto;
import UMC_8th.With_Run.map.dto.CourseCreateResponseDto;
import UMC_8th.With_Run.map.dto.PetFacilityResponseDto;
import UMC_8th.With_Run.map.dto.PlaceResponseDto;
import UMC_8th.With_Run.map.service.MapSearchService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "지도 API", description = "Swagger 테스트용 지도 관련 API")
@RestController
@RequestMapping("/api/maps")
@RequiredArgsConstructor
public class MapController {

    private final MapSearchService mapSearchService;

    @Operation(summary = "키워드 검색", description = "키워드로 장소를 검색합니다.")
    @GetMapping("/search/keyword")
    public ResponseEntity<List<PlaceResponseDto>> searchByKeyword(
            @Parameter(description = "검색 키워드", required = true)
            @RequestParam String query) {

        List<PlaceResponseDto> result = mapSearchService.searchPlacesByKeyword(query);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "카테고리 검색", description = "카테고리로 장소를 검색합니다.")
    @GetMapping("/search//categories")
    public ResponseEntity<List<PlaceResponseDto>> searchByCategory(
            @Parameter(description = "카테고리 타입 (예: 약국, 병원)", required = true)
            @RequestParam String type) {

        List<PlaceResponseDto> result = mapSearchService.searchPlacesByCategory(type);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "특정 시설 클릭 (상세 정보)", description = "선택한 장소의 상세 정보를 반환합니다.")
    @GetMapping("/places/{placeId}")
    public ResponseEntity<PlaceResponseDto> getPlaceDetail(
            @Parameter(description = "장소 ID", required = true)
            @PathVariable Long placeId) {

        PlaceResponseDto place = mapSearchService.getPlaceById(placeId);
        return ResponseEntity.ok(place);
    }


    @Operation(summary = "핀 생성")
    @PostMapping("/pins")
    public String createPin() {
        return "핀 생성 완료";
    }

    @Operation(summary = "핀 수정")
    @PatchMapping("/pins/{pinId}")
    public String updatePin(@PathVariable Long pinId) {
        return "핀 수정 완료 (pinId=" + pinId + ")";
    }

    @Operation(summary = "핀 삭제")
    @DeleteMapping("/pins/{pinId}")
    public String deletePin(@PathVariable Long pinId) {
        return "핀 삭제 완료 (pinId=" + pinId + ")";
    }

    @Operation(summary = "산책 코스 생성", description = "산책 코스를 등록합니다.")
    @PostMapping("/courses")
    public ResponseEntity<CourseCreateResponseDto> createCourse(
            @RequestHeader("Authorization") String accessToken,
            @RequestBody @Valid CourseCreateRequestDto requestDto) {

        Long courseId = mapSearchService.createCourse(accessToken, requestDto);

        return ResponseEntity.ok(
                CourseCreateResponseDto.builder()
                        .courseId(courseId)
                        .message("산책 코스가 성공적으로 등록되었어요!")
                        .build()
        );
    }



    @Operation(summary = "반려동물 시설 전체 조회", description = "모든 반려동물 시설 목록을 조회합니다.")
    @GetMapping("/pet-facilities")
    public ResponseEntity<List<PetFacilityResponseDto>> getAllPetFacilities() {
        List<PetFacilityResponseDto> facilities = mapSearchService.getAllPetFacilities();
        return ResponseEntity.ok(facilities);
    }
}
