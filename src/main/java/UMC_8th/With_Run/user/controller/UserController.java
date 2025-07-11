package UMC_8th.With_Run.user.controller;

import UMC_8th.With_Run.common.apiResponse.StndResponse;
import UMC_8th.With_Run.course.entity.Course;
import UMC_8th.With_Run.user.dto.UserRequestDto;
import UMC_8th.With_Run.user.dto.UserResponseDto;
import UMC_8th.With_Run.user.entity.Profile;
import UMC_8th.With_Run.user.entity.User;
import UMC_8th.With_Run.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.Mapping;
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

    @PostMapping("/login")
    @Operation(summary = "로그인 API", description = "로그인 API입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "TestSuccessCode", content = @Content(schema = @Schema(implementation = UserResponseDto.LoginResultDTO.class)))
    })
    @Parameters({
            @Parameter(name = "email", description = "사용자 이메일입니다.")
    })
    public void login(){
        //userService.login();
    }

    @PostMapping("/profile")
    @Operation(summary = "반려견 프로필 설정 API", description = "반려견의 프로필 정보를 설정하는 API입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "TestSuccessCode", content = @Content(schema = @Schema(implementation = Profile.class)))
    })
    @Parameters({
            @Parameter(name = "userId", description = "사용자 id 입니다.")
    })
    public void createBreedProfile(@RequestBody UserRequestDto.BreedProfileRequestDTO breedProfileRequestDTO){
        //userService.creatBreedProfile(breedProfileRequestDTO);
    }

    @PostMapping("/region")
    @Operation(summary = "동네 설정 API", description = "사용자의 동네 정보를 설정하는 API입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "TestSuccessCode", content = @Content(schema = @Schema(implementation = Profile.class)))
    })
    @Parameters({
            @Parameter(name = "userId", description = "사용자 id 입니다.")
    })
    public void createRegion(@RequestBody UserRequestDto.RegionRequestDTO RegionRequestDTO){
        //userService.createRegion(RegionRequestDTO);
    }

    @PostMapping("/alarm")
    @Operation(summary = "알람 끄기 API", description = "사용자의 알람을 끄는 API입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "TestSuccessCode", content = @Content(schema = @Schema(implementation = StndResponse.class)))
    })
    @Parameters({
            @Parameter(name = "userId", description = "사용자 id 입니다.")
    })
    public void turnOffAlarm(){
        //userService.turnOffAlarm();
    }

    @PatchMapping("/")
    @Operation(summary = "회원 탈퇴 API", description = "회원 탈퇴 API입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "TestSuccessCode", content = @Content(schema = @Schema(implementation = StndResponse.class)))
    })
    @Parameters({
            @Parameter(name = "userId", description = "사용자 id 입니다.")
    })
    public void cancelMembership(){
        //userService.cancelMembership();
    }

    @PostMapping("/logout")
    @Operation(summary = "로그아웃 API", description = "로그아웃 API입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "TestSuccessCode", content = @Content(schema = @Schema(implementation = StndResponse.class)))
    })
    @Parameters({
            @Parameter(name = "userId", description = "사용자 id 입니다.")
    })
    public void logout(){
        //userService.logout();
    }

    @GetMapping("/profile")
    @Operation(summary = "프로필 조회 API", description = "사용자의 기본 프로필을 조회하는 API입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "TestSuccessCode", content = @Content(schema = @Schema(implementation = UserResponseDto.ProfileResultDTO.class)))
    })
    @Parameters({
            @Parameter(name = "userId", description = "사용자 id 입니다.")
    })
    public void getProfile(){
        //userService.getProfile();
    }

    @GetMapping("/scraps")
    @Operation(summary = "스크랩 목록 조회 API", description = "사용자의 스크랩 목록을 조회하는 API입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "TestSuccessCode", content = @Content(schema = @Schema(implementation = UserResponseDto.ScrapListResultDTO.class)))
    })
    @Parameters({
            @Parameter(name = "userId", description = "사용자 id 입니다.")
    })
    public void getScrapList(){
        //userService.getScrapList();
    }

    @GetMapping("/likes")
    @Operation(summary = "좋아요 목록 조회 API", description = "사용자의 좋아요 목록을 조회하는 API입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "TestSuccessCode", content = @Content(schema = @Schema(implementation = UserResponseDto.LikeListResultDTO.class)))
    })
    @Parameters({
            @Parameter(name = "userId", description = "사용자 id 입니다.")
    })
    public void getLikeList(){
        //userService.getLikeList();
    }

    @GetMapping("/courses")
    @Operation(summary = "내코스 목록 조회 API", description = "사용자의 코스를 조회하는 API입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "TestSuccessCode", content = @Content(schema = @Schema(implementation = UserResponseDto.CourseListResultDTO.class)))
    })
    @Parameters({
            @Parameter(name = "userId", description = "사용자 id 입니다.")
    })
    public void getCourseList(){
        //userService.getCourseList();
    }

    @GetMapping("/followers")
    @Operation(summary = "팔로워 조회 API", description = "사용자의 팔로워를 조회하는 API입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "TestSuccessCode", content = @Content(schema = @Schema(implementation = UserResponseDto.FollowerListResultDTO.class)))
    })
    @Parameters({
            @Parameter(name = "userId", description = "사용자 id 입니다.")
    })
    public void getFollowerList(){
        //userService.getFollowerList();
    }

    @GetMapping("/followings")
    @Operation(summary = "팔로잉 조회 API", description = "사용자의 팔로우를 조회하는 API입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "TestSuccessCode", content = @Content(schema = @Schema(implementation = UserResponseDto.FollowingListResultDTO.class)))
    })
    @Parameters({
            @Parameter(name = "userId", description = "사용자 id 입니다.")
    })
    public void getFollowingList(){
        //userService.getFollowingList();
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
    public void cancelFollowing(@PathVariable("following_id") Long following_id){
        //userService.deleteFollowing(followingId);
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
    public void deleteFollower(@PathVariable Long follower_id){
        //userService.deleteFollower(followerId);
    }

    @PatchMapping("/profile")
    @Operation(summary = "프로필 수정 API", description = "사용자의 프로필을 수정하는 API입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "TestSuccessCode", content = @Content(schema = @Schema(implementation = Profile.class)))
    })
    @Parameters({
            @Parameter(name = "userId", description = "사용자 id 입니다.")
    })
    public void updateProfile(@RequestBody UserRequestDto.UpdateProfileDTO updateProfileDTO){
        //userService.updateProfile();
    }

    @PatchMapping("/courses/{course_id}")
    @Operation(summary = "코스 수정 API", description = "사용자의 코스를 수정하는 API입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "TestSuccessCode", content = @Content(schema = @Schema(implementation = Course.class)))
    })
    @Parameters({
            @Parameter(name = "userId", description = "사용자 id 입니다."),
            @Parameter(name = "courseId", description = "코스 id 입니다.")
    })
    public void updateCourse(@PathVariable Long course_id, @RequestBody UserRequestDto.UpdateCourseDTO updateCourseDTO){
        //userService.updateCourse();
    }

}
