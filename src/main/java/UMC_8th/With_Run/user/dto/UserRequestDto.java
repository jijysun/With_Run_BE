package UMC_8th.With_Run.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

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
        private Long provinceId;
        private Long cityId;
        private Long townId;
        private String name;
        private String gender;
        private String birth;
        private String breed;
        private String size;
        private List<String> characters;
        private List<String> style;
        private String introduction;
    }

    @Getter
    @Setter
    public static class UpdateProfileDTO {
        private Long provinceId;
        private Long cityId;
        private Long townId;
        private String name;
        private String gender;
        private String birth;
        private String breed;
        private String size;
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
    public static class UpdateCourseDTO {
        private String name;
        private String description;
        private String time;
        private List<String> keyWords;
        private List<Long> pinIds;
        private Long provinceId;
        private Long cityId;
        private Long townId;
    }

    @Getter
    @Setter
    public static class ProfileImageRequest {
        @Schema(description = "프로필 이미지 파일", type = "string", format = "binary")
        private MultipartFile file;
    }


}
