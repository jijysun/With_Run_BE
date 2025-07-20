package UMC_8th.With_Run.user.controller;

import UMC_8th.With_Run.common.apiResponse.StndResponse;
import UMC_8th.With_Run.common.apiResponse.status.SuccessCode;
import UMC_8th.With_Run.user.dto.UserRequestDto;
import UMC_8th.With_Run.user.dto.UserRequestDto.BreedProfileRequestDTO;
import UMC_8th.With_Run.user.dto.UserRequestDto.LoginRequestDTO;
import UMC_8th.With_Run.user.dto.UserRequestDto.RegionRequestDTO;
import UMC_8th.With_Run.user.dto.UserRequestDto.UpdateCourseDTO;
import UMC_8th.With_Run.user.dto.UserRequestDto.UpdateProfileDTO;
import UMC_8th.With_Run.user.dto.UserResponseDto;
import UMC_8th.With_Run.user.dto.UserResponseDto.CourseListResultDTO;
import UMC_8th.With_Run.user.dto.UserResponseDto.FollowerListResultDTO;
import UMC_8th.With_Run.user.dto.UserResponseDto.FollowingListResultDTO;
import UMC_8th.With_Run.user.dto.UserResponseDto.LikeListResultDTO;
import UMC_8th.With_Run.user.dto.UserResponseDto.ProfileResultDTO;
import UMC_8th.With_Run.user.dto.UserResponseDto.ScrapListResultDTO;
import UMC_8th.With_Run.user.dto.UserResponseDto.SimpleUserResultDTO;
import UMC_8th.With_Run.user.service.LikesService;
import UMC_8th.With_Run.user.service.ProfileService;
import UMC_8th.With_Run.user.service.ScrapService;
import UMC_8th.With_Run.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "사용자 API")
public class UserController {

    private final UserService userService;
    private final ProfileService profileService;
    private final LikesService likesService;
    private final ScrapService scrapService;

    @PostMapping("/login")
    @Operation(summary = "로그인 API", description = "로그인 API입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 성공", content = @Content(schema = @Schema(implementation = StndResponse.class)))
    })
    public StndResponse<UserResponseDto.LoginResultDTO> login(@RequestBody LoginRequestDTO request) {
        UserResponseDto.LoginResultDTO dto = userService.login(request);  // request 전달
        return StndResponse.onSuccess(dto, SuccessCode.LOGIN_SUCCESS);
    }

    @PostMapping("/profile")
    @Operation(summary = "반려견 프로필 설정 API", description = "반려견의 프로필 정보를 설정하는 API입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "TestSuccessCode", content = @Content(schema = @Schema(implementation = StndResponse.class)))
    })
    @Parameters({
            @Parameter(name = "userId", description = "사용자 id 입니다.")
    })
    public StndResponse<BreedProfileRequestDTO>  createBreedProfile(@RequestBody UserRequestDto.BreedProfileRequestDTO breedProfileRequestDTO){
        UserRequestDto.BreedProfileRequestDTO dto = new UserRequestDto.BreedProfileRequestDTO();
        return StndResponse.onSuccess(dto, SuccessCode.REQUEST_SUCCESS);
    }

    @PostMapping("/region")
    @Operation(summary = "동네 설정 API", description = "사용자의 동네 정보를 설정하는 API입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "TestSuccessCode", content = @Content(schema = @Schema(implementation = StndResponse.class)))
    })
    @Parameters({
            @Parameter(name = "userId", description = "사용자 id 입니다.")
    })
    public StndResponse<RegionRequestDTO> createRegion(@RequestBody UserRequestDto.RegionRequestDTO RegionRequestDTO){
        UserRequestDto.RegionRequestDTO dto = new UserRequestDto.RegionRequestDTO();
        return StndResponse.onSuccess(dto, SuccessCode.REQUEST_SUCCESS);
    }

    @PostMapping("/alarm")
    @Operation(summary = "알람 끄기 API", description = "사용자의 알람을 끄는 API입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "TestSuccessCode", content = @Content(schema = @Schema(implementation = StndResponse.class)))
    })
    @Parameters({
            @Parameter(name = "userId", description = "사용자 id 입니다.")
    })
    public SuccessCode turnOffAlarm(){
        return SuccessCode.REQUEST_SUCCESS;
    }

    @PatchMapping("/")
    @Operation(summary = "회원 탈퇴 API", description = "JWT 토큰을 바탕으로 본인의 계정을 탈퇴합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "탈퇴 성공", content = @Content(schema = @Schema(implementation = StndResponse.class)))
    })
    public StndResponse<SimpleUserResultDTO> cancelMembership(HttpServletRequest request) {
        userService.cancelMembership(request);
        return StndResponse.onSuccess(
                new SimpleUserResultDTO("탈퇴가 완료되었습니다."),
                SuccessCode.REQUEST_SUCCESS
        );
    }

    @PostMapping("/logout")
    @Operation(summary = "로그아웃 API", description = "로그아웃 API입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "TestSuccessCode", content = @Content(schema = @Schema(implementation = StndResponse.class)))
    })
    @Parameters({
            @Parameter(name = "userId", description = "사용자 id 입니다.")
    })
    public SuccessCode logout(){
        return SuccessCode.REQUEST_SUCCESS;
    }

    @GetMapping("/profile")
    @Operation(summary = "프로필 조회 API", description = "사용자의 기본 프로필을 조회하는 API입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공")
    })
    public StndResponse<ProfileResultDTO> getProfile(HttpServletRequest request){
        UserResponseDto.ProfileResultDTO dto = profileService.getProfileByCurrentUser(request);
        return StndResponse.onSuccess(dto, SuccessCode.INQUIRY_SUCCESS);
    }

    @GetMapping("/scraps")
    @Operation(summary = "스크랩 목록 조회 API", description = "JWT 기반으로 사용자의 스크랩 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = StndResponse.class)))
    })
    public StndResponse<ScrapListResultDTO> getScrapList(HttpServletRequest request) {
        ScrapListResultDTO dto = scrapService.getScrapsByCurrentUser(request);
        return StndResponse.onSuccess(dto, SuccessCode.INQUIRY_SUCCESS);
    }

    @GetMapping("/likes")
    @Operation(summary = "좋아요 목록 조회 API", description = "JWT 기반으로 사용자의 좋아요한 코스 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = StndResponse.class)))
    })
    public StndResponse<LikeListResultDTO> getLikeList(HttpServletRequest request) {
        LikeListResultDTO dto = likesService.getLikesByCurrentUser(request);
        return StndResponse.onSuccess(dto, SuccessCode.INQUIRY_SUCCESS);
    }


    @GetMapping("/courses")
    @Operation(summary = "내코스 목록 조회 API", description = "사용자의 코스를 조회하는 API입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "TestSuccessCode", content = @Content(schema = @Schema(implementation = StndResponse.class)))
    })
    @Parameters({
            @Parameter(name = "userId", description = "사용자 id 입니다.")
    })
    public StndResponse<CourseListResultDTO> getCourseList(){
        UserResponseDto.CourseListResultDTO dto = new UserResponseDto.CourseListResultDTO();
        return StndResponse.onSuccess(dto, SuccessCode.INQUIRY_SUCCESS);
    }

    @GetMapping("/followers")
    @Operation(summary = "팔로워 조회 API", description = "사용자의 팔로워를 조회하는 API입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "TestSuccessCode", content = @Content(schema = @Schema(implementation = StndResponse.class)))
    })
    @Parameters({
            @Parameter(name = "userId", description = "사용자 id 입니다.")
    })
    public StndResponse<FollowerListResultDTO> getFollowerList(){
        UserResponseDto.FollowerListResultDTO dto = new UserResponseDto.FollowerListResultDTO();
        return StndResponse.onSuccess(dto, SuccessCode.INQUIRY_SUCCESS);
    }

    @GetMapping("/followings")
    @Operation(summary = "팔로잉 조회 API", description = "사용자의 팔로우를 조회하는 API입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "TestSuccessCode", content = @Content(schema = @Schema(implementation = StndResponse.class)))
    })
    @Parameters({
            @Parameter(name = "userId", description = "사용자 id 입니다.")
    })
    public StndResponse<FollowingListResultDTO> getFollowingList(){
        UserResponseDto.FollowingListResultDTO dto = new UserResponseDto.FollowingListResultDTO();
        return StndResponse.onSuccess(dto, SuccessCode.INQUIRY_SUCCESS);
    }

    @DeleteMapping("/followings/{following_id}")
    @Operation(summary = "팔로잉 취소 API", description = "사용자가 팔로우를 취소하는 API입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "TestSuccessCode", content = @Content(schema = @Schema(implementation = StndResponse.class)))
    })
    @Parameters({
            @Parameter(name = "userId", description = "사용자 id 입니다."),
            @Parameter(name = "followingId", description = "팔로잉 id 입니다.")
    })
    public SuccessCode cancelFollowing(@PathVariable("following_id") Long following_id){
        return SuccessCode.REQUEST_SUCCESS;
    }

    @DeleteMapping("/followings/{follower_id}")
    @Operation(summary = "팔로워 삭제 API", description = "사용자의 팔로워를 삭제하는 API입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "TestSuccessCode", content = @Content(schema = @Schema(implementation = StndResponse.class)))
    })
    @Parameters({
            @Parameter(name = "userId", description = "사용자 id 입니다."),
            @Parameter(name = "followerId", description = "팔로워 id 입니다.")
    })
    public SuccessCode deleteFollower(@PathVariable Long follower_id){
        return SuccessCode.REQUEST_SUCCESS;
    }

    @PatchMapping("/profile")
    @Operation(summary = "프로필 수정 API", description = "사용자의 프로필을 수정하는 API입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "TestSuccessCode", content = @Content(schema = @Schema(implementation = StndResponse.class)))
    })
    @Parameters({
            @Parameter(name = "userId", description = "사용자 id 입니다.")
    })
    public StndResponse<UpdateProfileDTO> updateProfile(@RequestBody UpdateProfileDTO updateProfileDTO){
        UserRequestDto.UpdateProfileDTO dto = new UserRequestDto.UpdateProfileDTO();
        return StndResponse.onSuccess(dto, SuccessCode.REQUEST_SUCCESS);
    }

    @PatchMapping("/courses/{course_id}")
    @Operation(summary = "코스 수정 API", description = "사용자의 코스를 수정하는 API입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "TestSuccessCode", content = @Content(schema = @Schema(implementation = StndResponse.class)))
    })
    @Parameters({
            @Parameter(name = "userId", description = "사용자 id 입니다."),
            @Parameter(name = "courseId", description = "코스 id 입니다.")
    })
    public StndResponse<UpdateCourseDTO> updateCourse(@PathVariable Long course_id, @RequestBody UpdateCourseDTO updateCourseDTO){
        UserRequestDto.UpdateCourseDTO dto = new UserRequestDto.UpdateCourseDTO();
        return StndResponse.onSuccess(dto, SuccessCode.REQUEST_SUCCESS);
    }

}
