package UMC_8th.With_Run.map.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

public class MapRequestDTO {

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class PinRequestDto {
        private Long courseId;
        private String name;
        private String detail;
        private String color;
        private Double latitude;
        private Double longitude;
        private Integer pinOrder;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CourseCreateRequestDto {
        @NotBlank(message = "코스 이름은 필수입니다.")
        @Size(max = 100, message = "코스 이름은 100자를 초과할 수 없습니다.")
        private String name;

        @NotBlank(message = "코스 설명은 필수입니다.")
        private String description;

        @NotNull(message = "키워드는 필수입니다.")
        private List<String> key_words; // ERD와 일관성을 위해 keyWords에서 key_words로 변경했지만, 자바 컨벤션은 camelCase

        @NotNull(message = "시간 정보는 필수입니다.")
        private TimeInfo time;

        @NotNull(message = "지역 정보는 필수입니다.")
        @Size(min = 1, message = "최소 한 개 이상의 지역을 지정해야 합니다.")
        private List<String> regions;

        @NotNull(message = "핀 ID는 필수입니다.")
        @Size(min = 1, message = "최소 한 개 이상의 핀을 지정해야 합니다.")
        private List<Long> pinIds;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TimeInfo {
        private int hours;
        private int minutes;
    }
}
