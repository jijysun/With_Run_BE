package UMC_8th.With_Run.map.controller;

import UMC_8th.With_Run.common.apiResponse.StndResponse;
import UMC_8th.With_Run.common.apiResponse.status.SuccessCode;
import UMC_8th.With_Run.map.dto.MapRequestDTO;
import UMC_8th.With_Run.map.dto.MapResponseDTO;
import UMC_8th.With_Run.map.service.MapSearchService;
import UMC_8th.With_Run.map.service.PinService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;
import UMC_8th.With_Run.map.entity.PetFacility;

@Tag(name = "지도 API", description = "Swagger 테스트용 지도 관련 API")
@RestController
@RequestMapping("/api/maps")
@RequiredArgsConstructor
public class MapController {

    private final MapSearchService mapSearchService;
    private final PinService pinService;


<<<<<<< HEAD

    //검색 관련 API
=======
>>>>>>> 0730fe8 ([UPDATE] 카테고리기반 장소 검색 에러 해결 & 페이징 처리)
    @Operation(
            summary = "카테고리 기반 반려동물 시설 검색 (페이징)",
            description = "카테고리로 반려동물 시설을 페이징 형태로 검색합니다.",
            parameters = {
                    @Parameter(name = "type", description = "카테고리 타입 (예: 동물약국, 미술관)", required = true, example = "동물약국"),
                    @Parameter(name = "page", description = "페이지 번호 (0부터 시작, 최소 0)", required = false, example = "0"),
                    @Parameter(name = "size", description = "한 페이지당 항목 수 (1-100)", required = false, example = "20")
            }
    )
    @GetMapping("/search/categories")
    public StndResponse<MapResponseDTO.PetFacilityPageResponseDto> searchByCategory(
            @RequestParam String type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        MapResponseDTO.PetFacilityPageResponseDto result = 
                mapSearchService.searchPlacesByCategorySimple(type, page, size);

        return StndResponse.onSuccess(result, SuccessCode.INQUIRY_SUCCESS);
    }


    @Operation(
            summary = "ID 기반 반려동물 시설 검색",
            description = "ID로 특정 반려동물 시설의 상세 정보를 검색합니다.",
            parameters = {
                    @Parameter(name = "id", description = "조회할 반려동물 시설의 ID", required = true, example = "1")
            }
    )
    @GetMapping("/search/{id}")
    public StndResponse<MapResponseDTO.PetFacilityResponseDto> getPetFacilityById(@PathVariable Long id) {
        MapResponseDTO.PetFacilityResponseDto facility = mapSearchService.getPetFacilityById(id);
        return StndResponse.onSuccess(facility, SuccessCode.INQUIRY_SUCCESS);
    }



<<<<<<< HEAD
    // 핀 관련 API
=======

>>>>>>> 0730fe8 ([UPDATE] 카테고리기반 장소 검색 에러 해결 & 페이징 처리)
    @Operation(
            summary = "핀 생성",
            description = "새로운 핀을 생성합니다.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "핀 생성 요청 DTO", required = true)
    )
    @PostMapping("/pin")
    public StndResponse<MapResponseDTO.PinResponseDto> createPin(@RequestBody @Valid MapRequestDTO.PinRequestDto requestDto) {
        // 1. 서비스 레이어 호출, pinId를 반환 받음
        Long pinId = pinService.createPin(requestDto);

        // 2. 응답 DTO 생성
        MapResponseDTO.PinResponseDto responseDto = MapResponseDTO.PinResponseDto.builder()
                .pinId(pinId)
                .build();

        // 3. StndResponse에 DTO와 성공 코드를 담아 반환
        return StndResponse.onSuccess(responseDto, SuccessCode.REQUEST_SUCCESS);
    }

    @Operation(
            summary = "핀 단건 조회",
            description = "핀 ID로 특정 핀 정보를 조회합니다.",
            parameters = {
                    @Parameter(name = "pinId", description = "조회할 핀의 ID", required = true, example = "1")
            }
    )
    @GetMapping("/pin/{pinId}")
    public StndResponse<MapResponseDTO.GetPinDto> getPinById(@PathVariable Long pinId) {
        // 1. 서비스 레이어 호출 (PinServiceImpl의 getPinById 메서드)
        MapResponseDTO.GetPinDto responseDto = pinService.getPinById(pinId);

        // 2. StndResponse에 DTO와 성공 코드를 담아 반환
        return StndResponse.onSuccess(responseDto, SuccessCode.INQUIRY_SUCCESS);
    }

    @Operation(
            summary = "핀 수정",
            description = "기존 핀 정보를 수정합니다.",
            parameters = {
                    @Parameter(name = "pinId", description = "수정할 핀의 ID", required = true, example = "1")
            }
    )
    @PatchMapping("/pin/{pinId}")
    public StndResponse<MapResponseDTO.PinResponseDto> updatePin( // 반환 타입 변경
                                                                  @PathVariable Long pinId,
                                                                  @RequestBody @Valid MapRequestDTO.PinRequestDto requestDto) {

        // 1. 서비스 레이어 호출, 수정된 핀의 ID를 반환 받음
        Long updatedPinId = pinService.updatePin(pinId, requestDto);

        // 2. 응답 DTO 생성
        MapResponseDTO.PinResponseDto responseDto = MapResponseDTO.PinResponseDto.builder()
                .pinId(updatedPinId)
                .build();

        // 3. StndResponse에 DTO와 성공 코드를 담아 반환
        return StndResponse.onSuccess(responseDto, SuccessCode.UPDATE_SUCCESS);
    }

    @Operation(
            summary = "핀 삭제",
            description = "핀을 삭제합니다.",
            parameters = {
                    @Parameter(name = "pinId", description = "삭제할 핀의 ID", required = true, example = "1")
            }
    )
    @DeleteMapping("/pin/{pinId}")
    public StndResponse<MapResponseDTO.PinResponseDto> deletePin(@PathVariable Long pinId) {
        // 1. 서비스 레이어 호출, 삭제된 핀의 ID를 반환 받음
        Long deletedPinId = pinService.deletePin(pinId);

        // 2. 응답 DTO 생성
        MapResponseDTO.PinResponseDto responseDto = MapResponseDTO.PinResponseDto.builder()
                .pinId(deletedPinId)
                .build();

        // 3. StndResponse에 DTO와 성공 코드를 담아 반환
        return StndResponse.onSuccess(responseDto, SuccessCode.DELETE_SUCCESS);
    }


    //코스 관련 API
    @Operation(
            summary = "산책 코스 생성",
            description = "산책 코스를 등록합니다.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "산책 코스 생성 요청 DTO", required = true)
    )
    @PostMapping("/courses")
    public StndResponse<MapResponseDTO.CourseCreateResponseDto> createCourse(
            @RequestBody @Valid MapRequestDTO.CourseCreateRequestDto requestDto) {
        // 수정 부분: mapSearchService.createCourse 호출 방식 변경
        Long courseId = mapSearchService.createCourse(requestDto); // userId 파라미터 제거
        MapResponseDTO.CourseCreateResponseDto response = MapResponseDTO.CourseCreateResponseDto.builder()
                .courseId(courseId)
                .build();
        return StndResponse.onSuccess(response, SuccessCode.REQUEST_SUCCESS);
    }


}