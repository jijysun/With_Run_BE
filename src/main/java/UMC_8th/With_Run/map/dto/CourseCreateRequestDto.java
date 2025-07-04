package UMC_8th.With_Run.map.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class CourseCreateRequestDto {
    private String name;
    private String description;
    private String time;
    private List<String> keyWords;
    private List<String> regions;
    private List<Long> pinIds;
}

