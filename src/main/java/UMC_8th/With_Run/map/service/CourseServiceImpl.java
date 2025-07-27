// src/main/java/UMC_8th/With_Run/map/service/impl/CourseServiceImpl.java
package UMC_8th.With_Run.map.service;

import UMC_8th.With_Run.common.apiResponse.basecode.PinErrorCode;
import UMC_8th.With_Run.common.exception.GeneralException;
import UMC_8th.With_Run.map.dto.MapRequestDTO;
import UMC_8th.With_Run.map.entity.Course;
import UMC_8th.With_Run.map.entity.CoursePin;
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
        // 1. 사용자 ID는 이미 외부에서 인증되어 넘어온다고 가정합니다.

        // 2. 핀 유효성 검사
        List<Long> pinIds = requestDto.getPinIds();
        List<Pin> pins = pinRepository.findAllByIdIn(pinIds);

        if (pins.size() != pinIds.size()) {
            // GeneralException 생성자에 PinErrorCode.PIN_NOT_FOUND 객체 전달
            throw new GeneralException(PinErrorCode.PIN_NOT_FOUND);
        }

        // 3. 코스 엔티티 준비
        // image_eb1d31.png에 따르면 String.join의 delimiter 인자가 필요한 것 같습니다.
        // 현재 코드에 delimiter가 누락되어 있을 수 있습니다.
        // String keywordsString = String.join(",", requestDto.getKey_words()); // 수정 전 예시
        // String regionsString = String.join(", ", requestDto.getRegions());   // 수정 전 예시
        String keywordsString = String.join(",", requestDto.getKey_words()); // delimiter "," 추가
        String regionsString = String.join(", ", requestDto.getRegions());   // delimiter ", " 추가

        int totalMinutes = (requestDto.getTime().getHours() * 60) + requestDto.getTime().getMinutes();

        Course course = Course.builder()
                .name(requestDto.getName())
                .description(requestDto.getDescription())
                .keyWord(keywordsString)
                .location(regionsString) // location이 VARCHAR/TEXT 타입이라고 가정
                .timeInMinutes(totalMinutes)
                .userId(userId)
                .build();

        // 4. 코스 엔티티 저장
        course = courseRepository.save(course);

        // 5. 순서가 있는 핀을 위한 CoursePin 엔티티 생성 및 연결
        for (int i = 0; i < pinIds.size(); i++) {
            Long pinId = pinIds.get(i);
            Pin pin = pins.stream()
                    .filter(p -> p.getId().equals(pinId))
                    .findFirst()
                    .orElseThrow(() -> new GeneralException(PinErrorCode.PIN_NOT_FOUND)); // 동일하게 PinErrorCode 사용

            CoursePin coursePin = CoursePin.builder()
                    .pin(pin)
                    .pinOrder(i)
                    .build();
            course.addCoursePin(coursePin);
        }

        courseRepository.save(course);

        return course.getId();
    }
}