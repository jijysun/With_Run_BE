package UMC_8th.With_Run.user.dto;

import UMC_8th.With_Run.course.entity.Course;
import UMC_8th.With_Run.user.entity.Likes;
import UMC_8th.With_Run.user.entity.Scraps;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class UserResponseDto {
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoginResultDTO {
        Long memberId;
        String accessToken;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProfileResultDTO {
        private Long id;              // 프로필 ID
        private Long userId;          // 사용자 ID
        private Long townId;          // 동네 ID
        private Long cityId;          // 시/군/구 ID
        private Long provinceId;      // 도 ID

        private String name;          // 반려견 이름
        private String gender;        // 성별
        private String birth;         // 생일 (문자열 또는 Date로 변경 가능)
        private String breed;         // 품종
        private String size;          // 크기
        private String profileImage;  // 이미지 URL

        private String character;     // 성격 (JSON -> String or Map<String, Object>)
        private String style;         // 스타일 (JSON -> String or Map<String, Object>)
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ScrapItemDTO {
        private Long courseId;
        private LocalDateTime scrapedAt;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ScrapListResultDTO {
        private List<ScrapItemDTO> scrapList;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LikeItemDTO {
        private Long courseId;
        private Long count;
        private LocalDateTime likedAt;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LikeListResultDTO {
        private List<LikeItemDTO> likeList;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CourseListResultDTO {
        private ArrayList<Course> courses;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FollowerListResultDTO {
        private ArrayList<?> followers;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FollowingListResultDTO {
        private ArrayList<?> followings;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SimpleUserResultDTO {
        private String message;
    }

}
