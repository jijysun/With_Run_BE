package UMC_8th.With_Run.map.service;

import UMC_8th.With_Run.common.apiResponse.basecode.PinErrorCode;
import UMC_8th.With_Run.common.exception.GeneralException;
import UMC_8th.With_Run.map.dto.MapRequestDTO;
import UMC_8th.With_Run.map.entity.Course;
import UMC_8th.With_Run.map.entity.Pin;
import UMC_8th.With_Run.map.repository.CourseRepository;
import UMC_8th.With_Run.map.repository.PinRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final PinRepository pinRepository;

    @Override
    @Transactional
    public Long createCourse(Long userId, MapRequestDTO.CourseCreateRequestDto requestDto) {
        List<Long> pinIds = requestDto.getPinIds();
        List<Pin> pins = pinRepository.findAllByIdIn(pinIds);

        // 모든 핀이 존재하는지 확인
        if (pins.size() != pinIds.size()) {
            throw new GeneralException(PinErrorCode.PIN_NOT_FOUND);
        }

        // 코스 엔티티 준비
        String keywordsString = String.join(",", requestDto.getKey_words());
        String regionsString = String.join(", ", requestDto.getRegions());
        // Course 엔티티의 time 필드가 String이므로, DTO에서 받은 String 값을 그대로 사용
        String timeString = requestDto.getTime(); // MapRequestDTO.CourseCreateRequestDto의 time 필드를 String으로 가정

        Course course = Course.builder()
                .name(requestDto.getName())
                .description(requestDto.getDescription())
                .keyWord(keywordsString)
                .location(regionsString)
                .time(timeString) // String 타입의 time 값 설정
                .userId(userId)
                .build();

        // 먼저 Course를 저장하여 ID를 할당받습니다.
        courseRepository.save(course);

        // 핀 엔티티에 Course ID 연결 및 순서 설정
        for (int i = 0; i < pinIds.size(); i++) {
            Long pinId = pinIds.get(i);
            Pin pin = pins.stream()
                    .filter(p -> p.getId().equals(pinId))
                    .findFirst()
                    .orElseThrow(() -> new GeneralException(PinErrorCode.PIN_NOT_FOUND));

            pin.setCourseId(course.getId()); // Pin 엔티티의 courseId 필드에 Course의 ID 설정
            pin.setPinOrder(i);
        }

        // 모든 Pin 엔티티를 한 번에 저장합니다.
        pinRepository.saveAll(pins);

        return course.getId();
    }
}