package UMC_8th.With_Run.map.dto;

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
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class CourseCreateRequestDto {
        private String name;
        private String description;
        private String time;
        private List<String> keyWords;
        private List<String> regions;
        private List<Long> pinIds;
        private List<Long> regionIds;
        private Long userId;
    }
}
