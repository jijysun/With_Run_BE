package UMC_8th.With_Run.course.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class CourseDetailResponse {
    private Long id;
    private String name;
    private String imageUrl;
    private List<String> keywords;
    private String description;
    private Integer time;

    private List<PinResponse> pins;

    @Getter
    @Builder
    public static class PinResponse {
        private Long id;
        private String name;
        private String color;
        private double latitude;
        private double longitude;
        private String detail;
    }
}
