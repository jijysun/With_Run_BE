package UMC_8th.With_Run.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

public class UserRequestDto {

    @Getter
    @Setter
    public static class LoginRequestDTO{

        @NotBlank(message = "이메일은 필수입니다.")
        @Email(message = "올바른 이메일 형식이어야 합니다.")
        private String email;

        @NotBlank(message = "네이버 ID는 필수입니다.")
        private String naverId;

    }

    @Getter
    @Setter
    public static class BreedProfileRequestDTO {
        private Long townId;
        private Long cityId;
        private Long provinceId;
        private String name;
        private String gender;
        private String birth;
        private String breed;
        private String size;
        private String profileImage;
        private List<String> characters;
        private List<String> style;
        private String introduction;
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
