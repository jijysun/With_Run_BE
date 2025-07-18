package UMC_8th.With_Run.map.controller;

import UMC_8th.With_Run.common.apiResponse.StndResponse;
import UMC_8th.With_Run.common.apiResponse.status.SuccessCode;
import UMC_8th.With_Run.map.dto.MapRequestDTO;
import UMC_8th.With_Run.map.dto.MapResponseDTO;
import UMC_8th.With_Run.map.service.MapSearchService;
import UMC_8th.With_Run.map.service.PinService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
    private final PinService pinService;


    @Operation(
        summary = "키워드 검색",
        description = "키워드로 장소를 검색합니다.",
        parameters = {
            @Parameter(name = "query", description = "검색 키워드", required = true, example = "약국")
        }
    )
    @GetMapping("/search/keyword")
    public StndResponse<List<MapResponseDTO.PlaceResponseDto>> searchByKeyword(
            @RequestParam String query) {

        List<MapResponseDTO.PlaceResponseDto> result = mapSearchService.searchPlacesByKeyword(query);
        return StndResponse.onSuccess(result, SuccessCode.INQUIRY_SUCCESS);
    }



    @Operation(
        summary = "카테고리 검색",
        description = "카테고리로 장소를 검색합니다.",
        parameters = {
            @Parameter(name = "type", description = "카테고리 타입 (예: 약국, 병원)", required = true, example = "약국")
        }
    )
    @GetMapping("/search/categories")
    public StndResponse<List<MapResponseDTO.PlaceResponseDto>> searchByCategory(
            @RequestParam String type) {

        List<MapResponseDTO.PlaceResponseDto> result = mapSearchService.searchPlacesByCategory(type);
        return StndResponse.onSuccess(result, SuccessCode.INQUIRY_SUCCESS);
    }



    @Operation(
            summary = "특정 시설 클릭 (상세 정보)",
            description = "선택한 장소의 상세 정보를 반환합니다.",
            parameters = {
                    @Parameter(name = "placeName", description = "장소 이름", required = true, example = "연남약국")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "정보 조회에 성공했습니다.",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = MapResponseDTO.PlaceResponseDto.class))),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청"),
                    @ApiResponse(responseCode = "404", description = "장소를 찾을 수 없음"),
                    @ApiResponse(responseCode = "500", description = "서버 오류")
            }
    )
    @GetMapping("/places/detail")
    public ResponseEntity<MapResponseDTO.PlaceResponseDto> getPlaceDetail(@RequestParam String placeName) {
        MapResponseDTO.PlaceResponseDto detail = mapSearchService.getPlaceDetailByName(placeName);
        if (detail == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(detail);
    }



    @Operation(
        summary = "핀 생성",
        description = "새로운 핀을 생성합니다.",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "핀 생성 요청 DTO",
            required = true
        )
    )
    @PostMapping("/pins")
    public StndResponse<MapResponseDTO.PinResponseDto> createPin(@RequestBody MapRequestDTO.PinRequestDto requestDto) {
        MapResponseDTO.PinResponseDto pin = pinService.createPin(requestDto);
        return StndResponse.onSuccess(pin, SuccessCode.CREATE_SUCCESS);
    }



    @Operation(
        summary = "핀 수정",
        description = "기존 핀 정보를 수정합니다.",
        parameters = {
            @Parameter(name = "pinId", description = "수정할 핀의 ID", required = true, example = "1")
        },
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "핀 수정 요청 DTO",
            required = true
        )
    )
    @PatchMapping("/pins/{pinId}")
    public StndResponse<MapResponseDTO.PinResponseDto> updatePin(
            @PathVariable Long pinId,
            @RequestBody MapRequestDTO.PinRequestDto requestDto) {
        MapResponseDTO.PinResponseDto updated = pinService.updatePin(pinId, requestDto);
        return StndResponse.onSuccess(updated, SuccessCode.UPDATE_SUCCESS);
    }



    @Operation(
        summary = "핀 삭제",
        description = "핀을 삭제합니다.",
        parameters = {
            @Parameter(name = "pinId", description = "삭제할 핀의 ID", required = true, example = "1")
        }
    )
    @DeleteMapping("/pins/{pinId}")
    public StndResponse<MapResponseDTO.PinResponseDto> deletePin(@PathVariable Long pinId) {
        MapResponseDTO.PinResponseDto deleted = pinService.deletePin(pinId);
        return StndResponse.onSuccess(deleted, SuccessCode.DELETE_SUCCESS);
    }



    @Operation(
        summary = "산책 코스 생성",
        description = "산책 코스를 등록합니다.",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "산책 코스 생성 요청 DTO",
            required = true
        ),
        parameters = {
            @Parameter(name = "Authorization", description = "액세스 토큰 (Bearer {token})", required = true, example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
        }
    )
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



    @Operation(
        summary = "반려동물 시설 전체 조회",
        description = "모든 반려동물 시설 목록을 조회합니다."
    )
    @GetMapping("/pet-facilities")
    public StndResponse<List<MapResponseDTO.PetFacilityResponseDto>> getAllPetFacilities() {
        List<MapResponseDTO.PetFacilityResponseDto> facilities = mapSearchService.getAllPetFacilities();
        return StndResponse.onSuccess(facilities, SuccessCode.INQUIRY_SUCCESS);
    }
}
