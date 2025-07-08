package UMC_8th.With_Run.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
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

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ScrapListResultDTO {
        private Long id;
        private Long userId;
        private Long courseId;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LikeListResultDTO {
        private Long id;
        private Long userId;
        private Long courseId;
        private Integer count;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CourseListResultDTO {
        private Long id;
        private String name;
        private String keyword;
        private String time;
        private String location;
        private String description;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FollowerListResultDTO {
        private Long id;
        private Long userId;
        private Long targetUserId;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FollowingListResultDTO {
        private Long id;
        private Long userId;
        private Long targetUserId;
    }
}
