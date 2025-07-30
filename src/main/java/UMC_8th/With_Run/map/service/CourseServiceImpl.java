package UMC_8th.With_Run.map.service;

import UMC_8th.With_Run.common.exception.GeneralException;
import UMC_8th.With_Run.course.entity.Course;
import UMC_8th.With_Run.course.repository.CourseRepository;
import UMC_8th.With_Run.map.dto.MapRequestDTO;
import UMC_8th.With_Run.map.entity.Pin;
import UMC_8th.With_Run.map.repository.PinRepository;
import UMC_8th.With_Run.user.entity.User;
import UMC_8th.With_Run.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final PinRepository pinRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public Long createCourse(Long userId, MapRequestDTO.CourseCreateRequestDto requestDto) {
        List<Long> pinIds = requestDto.getPinIds();

        List<Pin> foundPins = pinRepository.findAllById(pinIds);

        if (foundPins.size() != pinIds.size()) {
            throw new GeneralException(new CustomPinNotFoundErrorCode());
        }

        String keywordsString = String.join(",", requestDto.getKeyWords());
        String timeString = requestDto.getTime();

        // 수정 부분 시작: regionIds 필드를 사용하여 locationId 설정
        Long locationId = null;
        if (requestDto.getRegionIds() != null && !requestDto.getRegionIds().isEmpty()) {
            // regionIds 리스트의 첫 번째 요소를 locationId로 사용
            locationId = requestDto.getRegionIds().get(0);
        }
        // 수정 부분 끝

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("해당 유저가 존재하지 않습니다."));


        Course course = Course.builder()
                .name(requestDto.getName())
                .description(requestDto.getDescription())
                .keyWord(keywordsString)
                .time(timeString)
                .user(user)
                .createdAt(LocalDateTime.now())
                .build();

        courseRepository.save(course);

        Map<Long, Pin> pinMap = foundPins.stream()
                .collect(Collectors.toMap(Pin::getId, pin -> pin));

        for (Long pinId : pinIds) {
            Pin pin = pinMap.get(pinId);

            if (pin == null) {
                throw new GeneralException(new CustomPinNotFoundErrorCode());
            }
            pin.setCourseId(course.getId());
            pin.setUpdatedAt(LocalDateTime.now());
        }

        pinRepository.saveAll(foundPins);

        return course.getId();
    }

    private static class CustomPinNotFoundErrorCode implements UMC_8th.With_Run.common.apiResponse.basecode.BaseErrorCode {
        @Override
        public UMC_8th.With_Run.common.apiResponse.dto.ErrorReasonDTO getReason() {
            return UMC_8th.With_Run.common.apiResponse.dto.ErrorReasonDTO.builder()
                    .code("PIN4001")
                    .message("핀을 찾을 수 없습니다.")
                    .isSuccess(false)
                    .build();
        }

        @Override
        public UMC_8th.With_Run.common.apiResponse.dto.ErrorReasonDTO getReasonHttpStatus() {
            return UMC_8th.With_Run.common.apiResponse.dto.ErrorReasonDTO.builder()
                    .code("PIN4001")
                    .message("핀을 찾을 수 없습니다.")
                    .isSuccess(false)
                    .httpStatus(org.springframework.http.HttpStatus.NOT_FOUND)
                    .build();
        }
    }

    private static class CustomInvalidRegionCodeError implements UMC_8th.With_Run.common.apiResponse.basecode.BaseErrorCode {
        @Override
        public UMC_8th.With_Run.common.apiResponse.dto.ErrorReasonDTO getReason() {
            return UMC_8th.With_Run.common.apiResponse.dto.ErrorReasonDTO.builder()
                    .code("MAP4002")
                    .message("유효하지 않은 지역 코드 형식입니다.")
                    .isSuccess(false)
                    .build();
        }

        @Override
        public UMC_8th.With_Run.common.apiResponse.dto.ErrorReasonDTO getReasonHttpStatus() {
            return UMC_8th.With_Run.common.apiResponse.dto.ErrorReasonDTO.builder()
                    .code("MAP4002")
                    .message("유효하지 않은 지역 코드 형식입니다.")
                    .isSuccess(false)
                    .httpStatus(org.springframework.http.HttpStatus.BAD_REQUEST)
                    .build();
        }
    }
}