package UMC_8th.With_Run.friend.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.*;

@Tag(name = "친구 API", description = "친구 추천, 상세 조회, 팔로우, 차단, 검색 등 친구 관련 기능 제공")
@RestController
@RequestMapping("/api/friends")
public class FriendController {

    @Operation(summary = "추천 친구 조회", description = "사용자에게 맞는 추천 친구들의 간단한 프로필 정보를 조회합니다.")
    @GetMapping("/recommendation")
    public String getRecommendedFriends() {
        return "추천 친구 목록";
    }

    @Operation(summary = "사용자 세부 정보 조회", description = "특정 친구의 상세 정보를 조회합니다.")
    @GetMapping("/detail")
    public String getUserDetail(@RequestParam Long userId) {
        return "친구 상세 정보 (userId=" + userId + ")";
    }

    @Operation(summary = "동네 친구 전체 목록 조회", description = "동네의 모든 친구 목록을 조회합니다.")
    @GetMapping("/all")
    public String getAllFriends() {
        return "전체 친구 목록";
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

    @Operation(summary = "친구 검색", description = "이름 또는 ID 등으로 친구를 검색합니다.")
    @PostMapping("/search")
    public String searchFriend(@RequestParam String keyword) {
        return "검색 결과 (keyword=" + keyword + ")";
    }
}
