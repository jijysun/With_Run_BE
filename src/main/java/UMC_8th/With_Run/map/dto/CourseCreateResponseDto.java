package UMC_8th.With_Run.map.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CourseCreateResponseDto {
    private Long courseId;
    private String message;
}

