// src/main/java/UMC_8th/With_Run/map/service/PinServiceImpl.java
package UMC_8th.With_Run.map.service;

import UMC_8th.With_Run.map.dto.MapRequestDTO;
import UMC_8th.With_Run.map.dto.MapResponseDTO;
import UMC_8th.With_Run.map.entity.Course;
import UMC_8th.With_Run.map.entity.Pin;
import UMC_8th.With_Run.map.repository.CourseRepository;
import UMC_8th.With_Run.map.repository.PinRepository;
import UMC_8th.With_Run.common.exception.GeneralException;
import UMC_8th.With_Run.common.apiResponse.basecode.PinErrorCode;
import UMC_8th.With_Run.common.apiResponse.basecode.CourseErrorCode; // CourseErrorCode 임포트

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PinServiceImpl implements PinService {

    private final PinRepository pinRepository;
    private final CourseRepository courseRepository;

    @Override
    @Transactional
    public void createPin(MapRequestDTO.PinRequestDto requestDto) {
        // Course 엔티티를 찾아서 매핑
        // CourseErrorCode가 없다면 새로 생성하거나 PinErrorCode에 추가해야 함.
        Course course = courseRepository.findById(requestDto.getCourseId())
                .orElseThrow(() -> new GeneralException(CourseErrorCode.COURSE_NOT_FOUND)); // CourseErrorCode 사용

        Pin pin = Pin.builder()
                .course(course) // Course 엔티티 직접 연결
                .name(requestDto.getName())
                .detail(requestDto.getDetail())
                .color(requestDto.getColor())
                .latitude(requestDto.getLatitude())
                .longitude(requestDto.getLongitude())
                .pinOrder(requestDto.getPinOrder()) // DTO에 pinOrder 필드가 있으므로 직접 사용
                .createdAt(LocalDateTime.now()) // createdAt은 서비스 계층에서 설정 (엔티티의 Auditing 기능을 사용해도 됨)
                .build();

        pinRepository.save(pin);
    }

    @Override
    @Transactional
    public void updatePin(Long pinId, MapRequestDTO.PinRequestDto requestDto) { // 파라미터 타입 MapRequestDTO.PinRequestDto 로 통일
        Pin pin = pinRepository.findById(pinId)
                .orElseThrow(() -> new GeneralException(PinErrorCode.PIN_NOT_FOUND));

        // CourseId가 변경될 수 있으므로, Course 엔티티를 다시 찾아 설정
        if (requestDto.getCourseId() != null && !pin.getCourse().getId().equals(requestDto.getCourseId())) {
            Course newCourse = courseRepository.findById(requestDto.getCourseId())
                    .orElseThrow(() -> new GeneralException(CourseErrorCode.COURSE_NOT_FOUND));
            pin.setCourse(newCourse);
        }

        // DTO에서 받은 값으로 업데이트, null 체크하여 부분 업데이트 가능하도록
        if (requestDto.getName() != null) pin.setName(requestDto.getName());
        if (requestDto.getDetail() != null) pin.setDetail(requestDto.getDetail());
        if (requestDto.getColor() != null) pin.setColor(requestDto.getColor());
        if (requestDto.getLatitude() != null) pin.setLatitude(requestDto.getLatitude());
        if (requestDto.getLongitude() != null) pin.setLongitude(requestDto.getLongitude());
        if (requestDto.getPinOrder() != null) pin.setPinOrder(requestDto.getPinOrder()); // pinOrder 업데이트
        pin.setUpdatedAt(LocalDateTime.now()); // updatedAt은 서비스 계층에서 설정 (엔티티의 Auditing 기능을 사용해도 됨)

        pinRepository.save(pin);
    }

    @Override
    @Transactional
    public void deletePin(Long pinId) {
        Pin pin = pinRepository.findById(pinId)
                .orElseThrow(() -> new GeneralException(PinErrorCode.PIN_NOT_FOUND));

        // 실제 삭제 대신 deletedAt 필드를 업데이트하는 "소프트 삭제" 방식
        pin.setDeletedAt(LocalDateTime.now());
        pinRepository.save(pin);
    }

    @Override
    public MapResponseDTO.PinResponseDto getPinById(Long pinId) {
        Pin pin = pinRepository.findById(pinId)
                .orElseThrow(() -> new GeneralException(PinErrorCode.PIN_NOT_FOUND));

        return fromEntity(pin);
    }

    // Pin 엔티티를 PinResponseDto로 변환하는 헬퍼 메서드
    public static MapResponseDTO.PinResponseDto fromEntity(Pin pin) {
        return MapResponseDTO.PinResponseDto.builder()
                .pinId(pin.getId())
                .courseId(pin.getCourse().getId()) // Course 객체에서 ID 가져오기
                .name(pin.getName())
                .detail(pin.getDetail())
                .color(pin.getColor())
                .latitude(pin.getLatitude())
                .longitude(pin.getLongitude())
                .pinOrder(pin.getPinOrder()) // pinOrder 추가
                .build();
    }
}