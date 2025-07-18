package UMC_8th.With_Run.user.dto;

import UMC_8th.With_Run.course.entity.Course;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

public class UserRequestDto {

    @Getter
    @Setter
    public static class LoginRequestDTO{
        //추후 로그인 Request 형식은 바뀔 수 있음
        @NotBlank(message = "이메일은 필수입니다.")
        @Email(message = "올바른 이메일 형식이어야 합니다.")
        private String email;

    }

    @Getter
    @Setter
    public static class BreedProfileRequestDTO{
        private String name;
        private String gender;
        private String birth;
        private String breed;
        private String size;
        private String profileImage;
        private String characters;
        private String style;
    }

    @Getter
    @Setter
    public static class RegionRequestDTO{
        private Long townId;
        private Long cityId;
        private Long provinceId;
    }

    @Getter
    @Setter
    public static class UpdateProfileDTO{
        private String profileImage;
    }

    @Getter
    @Setter
    public static class UpdateCourseDTO{
        private Long courseId;
        //코스 엔티티 나오면 핀 데이터 추가할 예정
        //private ArrayList<Pin> pinList;
        private String courseName;
        private String courseDescription;
        private String courseImage;
        private String keyword;
        private Integer hour;
        private Integer minute;
        private Long townId;
        private Long cityId;
        private Long provinceId;
    }


}
