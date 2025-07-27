// src/main/java/UMC_8th/With_Run/map/service/impl/CourseServiceImpl.java
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
            throw new GeneralException(PinErrorCode.PIN_NOT_FOUND); // PinErrorCode 사용
        }

        // 코스 엔티티 준비
        String keywordsString = String.join(",", requestDto.getKey_words());
        String regionsString = String.join(", ", requestDto.getRegions());
        int totalMinutes = (requestDto.getTime().getHours() * 60) + requestDto.getTime().getMinutes();

        Course course = Course.builder()
                .name(requestDto.getName())
                .description(requestDto.getDescription())
                .keyWord(keywordsString)
                .location(regionsString)
                .timeInMinutes(totalMinutes)
                .userId(userId)
                .build();

        // 핀 엔티티에 Course 연결 및 순서 설정
        for (int i = 0; i < pinIds.size(); i++) {
            Long pinId = pinIds.get(i);
            Pin pin = pins.stream()
                    .filter(p -> p.getId().equals(pinId))
                    .findFirst()
                    .orElseThrow(() -> new GeneralException(PinErrorCode.PIN_NOT_FOUND)); // PinErrorCode 사용

            pin.setCourse(course);
            pin.setPinOrder(i);
            course.addPin(pin);
        }

        courseRepository.save(course);

        return course.getId();
    }
}