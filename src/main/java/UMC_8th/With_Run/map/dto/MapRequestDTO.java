package UMC_8th.With_Run.map.dto;

import lombok.*;

import java.util.List;

public class MapRequestDTO {


    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CourseCreateRequestDto {
        private String name;
        private String description;
        private List<String> key_words;
        private List<String> regions;
        // Course 엔티티의 time이 String이므로 DTO도 String으로 변경
        private String time;
        private List<Long> pinIds;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PinRequestDto {
        private Long courseId;
        private String name;
        private String detail;
        private String color;
        private Double latitude;
        private Double longitude;
        private Integer pinOrder;
        // image_e32a25.png 및 DB 스키마(image_e2c4e3.png)에 따라 추가
        private Long userId;
        private Long provinceId;
        private Long cityId;
        private Long townId;
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
