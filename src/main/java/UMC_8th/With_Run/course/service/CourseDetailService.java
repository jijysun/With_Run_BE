package UMC_8th.With_Run.course.service;

import UMC_8th.With_Run.course.dto.CourseDetailResponse;
import UMC_8th.With_Run.course.dto.CourseDetailResponse.PinResponse;
import UMC_8th.With_Run.course.entity.Course;
import UMC_8th.With_Run.map.entity.Pin;
import UMC_8th.With_Run.course.repository.CourseRepository;
import UMC_8th.With_Run.map.repository.PinRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseDetailService {

    private final CourseRepository courseRepository;
    private final PinRepository pinRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Transactional(readOnly = true)
    public CourseDetailResponse getCourseDetail(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("해당 코스를 찾을 수 없습니다."));

        List<String> keywords;
        try {
            keywords = objectMapper.readValue(course.getKeyWord(), new TypeReference<>() {});
        } catch (Exception e) {
            keywords = Collections.emptyList();
        }

        // 핀을 pinOrder 오름차순으로 정렬해서 조회
        List<Pin> pins = pinRepository.findByCourseIdOrderByPinOrderAsc(courseId);

        List<CourseDetailResponse.PinResponse> pinResponses = pins.stream()
                .map(pin -> CourseDetailResponse.PinResponse.builder()
                        .id(pin.getId())
                        .name(pin.getName())
                        .color(pin.getColor())  // 중복 제거
                        .latitude(pin.getLatitude())
                        .longitude(pin.getLongitude())
                        .detail(pin.getDetail())
                        .build())
                .collect(Collectors.toList());

        return CourseDetailResponse.builder()
                .id(course.getId())
                .name(course.getName())
                .imageUrl(course.getCourseImage())
                .description(course.getDescription())
                .keywords(keywords)
                .time(course.getTime())
                .pins(pinResponses)
                .build();
    }
}
