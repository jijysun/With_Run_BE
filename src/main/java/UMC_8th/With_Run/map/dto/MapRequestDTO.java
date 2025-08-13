package UMC_8th.With_Run.map.dto;

import UMC_8th.With_Run.user.dto.UserRequestDto;
import lombok.*;

import java.util.List;

public class MapRequestDTO {

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class PinRequestDto {
        private String name;
        private String detail;
        private String color;
        private Double latitude;
        private Double longitude;
        private Integer pinOrder;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class RegionRequestDto {
        private Long id;
        private String name;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class CourseCreateRequestDto {
        private String name;
        private String description;
        private Integer time;
        private List<String> keyWords;
        private List<PinRequestDto> pins;
        private Long userId;
        private Long regionProvinceId;
        private Long regionsCityId;
        private Long regionsTownId;
        private String overviewPolyline;
    }
}
