package UMC_8th.With_Run.friend.controller;

import UMC_8th.With_Run.friend.dto.FriendsResponse;
import UMC_8th.With_Run.friend.dto.FriendDetailResponse;
import UMC_8th.With_Run.friend.service.AllFriendsService;
import UMC_8th.With_Run.friend.service.FriendDetailService;
import UMC_8th.With_Run.friend.service.RecommendedFriendsService;
import UMC_8th.With_Run.friend.service.SearchFriendsService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "친구 API", description = "친구 추천, 상세 조회, 팔로우, 차단, 검색 등 친구 관련 기능 제공")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/friends")
public class FriendController {

    private final AllFriendsService allFriendsService;
    private final FriendDetailService friendDetailService;
    private final RecommendedFriendsService recommendedFriendsService;
    private final SearchFriendsService searchFriendsService;

    @Operation(summary = "추천 친구 조회", description = "사용자에게 맞는 추천 친구들의 간단한 프로필 정보를 조회합니다.")
    @GetMapping("/recommendation")
    public List<FriendsResponse> getRecommendedFriends(
            @RequestParam(required = true) Long provinceId,
            @RequestParam(required = false) Long cityId,
            @RequestParam(required = false) Long townId) {
        Long userId = 1L; // 하드코딩 유저

        return recommendedFriendsService.recommendedFriends(provinceId, cityId, townId, userId);
    }

    @Operation(summary = "사용자 세부 정보 조회", description = "특정 친구의 상세 정보를 조회합니다.")
    @GetMapping("/detail")
    public FriendDetailResponse getUserDetail(@RequestParam Long userId) {
        return friendDetailService.getFriendDetail(userId);
    }

    @Operation(summary = "동네 친구 전체 목록 조회", description = "동네의 모든 친구 목록을 조회합니다.")
    @GetMapping("/all")
    public List<FriendsResponse> getFriendsByRegion(
            @RequestParam(value = "provinceId", required = true) Long provinceId,
            @RequestParam(value = "cityId", required = false) Long cityId,
            @RequestParam(value = "townId", required = false) Long townId
    )
    {
        Long userId = 1L; // 🔧 하드코딩된 유저 ID
        return allFriendsService.findUsersByRegion(provinceId, cityId, townId, userId);
    }

    @Operation(summary = "팔로우", description = "특정 사용자를 팔로우합니다.")
    @PostMapping("/follow")
    public String followUser(@RequestParam Long userId) {
        return "팔로우 완료 (userId=" + userId + ")";
    }

    @Operation(summary = "차단", description = "특정 사용자를 차단합니다.")
    @PostMapping("/block")
    public String blockUser(@RequestParam Long userId) {
        return "차단 완료 (userId=" + userId + ")";
    }

    @Operation(summary = "신고", description = "특정 사용자를 신고합니다.")
    @PostMapping("/report")
    public String reportUser(@RequestParam Long userId, @RequestParam String reason) {
        return "신고 완료 (userId=" + userId + ", reason=" + reason + ")";
    }

    @Operation(summary = "친구 검색", description = "키워드로 친구를 검색합니다.")
    @GetMapping("/search")
    public List<FriendsResponse> searchFriends(
            @RequestParam(value = "provinceId") Long provinceId,
            @RequestParam(value = "cityId", required = false) Long cityId,
            @RequestParam(value = "townId", required = false) Long townId,
            @RequestParam String keyword
    ) {
        // 현재 로그인된 유저 id (임시로 1L 하드코딩)
        Long userId = 1L;

        return searchFriendsService.searchFriends(provinceId, cityId, townId, userId, keyword);
    }

}
