package UMC_8th.With_Run.map.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

@Api(tags = "Swagger 테스트용 지도 관련 API")
@RestController
@RequestMapping("/api/maps")


public class MapController {

    @ApiOperation(value = "카테고리 검색")
    @GetMapping("/categories")
    public String getCategories(@RequestParam String type) {
        return "카테고리 검색 (type=" + type + ")";
    }

    @ApiOperation(value = "특정 시설 클릭 (상세 정보)")
    @GetMapping("/pet-facilities/{id}")
    public String getFacilityById(@PathVariable Long id) {
        return "시설 상세정보 (id=" + id + ")";
    }

    @ApiOperation(value = "검색")
    @GetMapping("/search")
    public String search(@RequestParam String keyword) {
        return "검색 결과 (keyword=" + keyword + ")";
    }

    @ApiOperation(value = "핀 생성")
    @PostMapping("/pins")
    public String createPin() {
        return "핀 생성 완료";
    }

    @ApiOperation(value = "핀 수정")
    @PatchMapping("/pins/{pinId}")
    public String updatePin(@PathVariable Long pinId) {
        return "핀 수정 완료 (pinId=" + pinId + ")";
    }

    @ApiOperation(value = "핀 삭제")
    @DeleteMapping("/pins/{pinId}")
    public String deletePin(@PathVariable Long pinId) {
        return "핀 삭제 완료 (pinId=" + pinId + ")";
    }

    @ApiOperation(value = "코스 생성")
    @PostMapping("/courses")
    public String createCourse() {
        return "코스 생성 완료";
    }

    @ApiOperation(value = "반려동물시설 전체 조회")
    @GetMapping("/pet-facilities")
    public String getAllPetFacilities() {
        return "전체 반려동물시설 목록";
    }



}
