package UMC_8th.With_Run.map.service;

import UMC_8th.With_Run.common.apiResponse.basecode.PinErrorCode;
import UMC_8th.With_Run.common.exception.GeneralException;
import UMC_8th.With_Run.map.dto.MapRequestDTO;
import UMC_8th.With_Run.map.dto.MapResponseDTO;
import UMC_8th.With_Run.map.entity.Pin;
import UMC_8th.With_Run.map.repository.CourseRepository;
import UMC_8th.With_Run.map.repository.PinRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PinServiceImpl implements PinService {

    private final PinRepository pinRepository;
    private final CourseRepository courseRepository; // PinService에서 Course 정보를 조회할 필요가 있다면 유지, 아니면 제거

    @Override
    @Transactional
    public void createPin(MapRequestDTO.PinRequestDto requestDto) {
        Pin pin = Pin.builder()
                .courseId(requestDto.getCourseId()) // Pin 엔티티의 courseId 필드에 바로 설정
                .name(requestDto.getName())
                .detail(requestDto.getDetail())
                .color(requestDto.getColor())
                .latitude(requestDto.getLatitude())
                .longitude(requestDto.getLongitude())
                .pinOrder(requestDto.getPinOrder())
                .userId(requestDto.getUserId()) // DTO에 해당 필드가 있다고 가정
                .provinceId(requestDto.getProvinceId()) // DTO에 해당 필드가 있다고 가정
                .cityId(requestDto.getCityId()) // DTO에 해당 필드가 있다고 가정
                .townId(requestDto.getTownId()) // DTO에 해당 필드가 있다고 가정
                .build();

        // @CreatedDate를 사용하는 경우 아래 라인은 제거
        if (pin.getCreatedAt() == null) {
            pin.setCreatedAt(LocalDateTime.now());
        }

        pinRepository.save(pin);
    }

    @Override
    @Transactional
    public void updatePin(Long pinId, MapRequestDTO.PinRequestDto requestDto) {
        Pin pin = pinRepository.findById(pinId)
                .orElseThrow(() -> new GeneralException(PinErrorCode.PIN_NOT_FOUND));

        // courseId 변경 로직: Pin 엔티티의 courseId 필드를 직접 업데이트
        if (requestDto.getCourseId() != null && !pin.getCourseId().equals(requestDto.getCourseId())) {
            pin.setCourseId(requestDto.getCourseId());
        }

        // DTO에서 받은 값으로 업데이트, null 체크하여 부분 업데이트 가능하도록
        if (requestDto.getName() != null) pin.setName(requestDto.getName());
        if (requestDto.getDetail() != null) pin.setDetail(requestDto.getDetail());
        if (requestDto.getColor() != null) pin.setColor(requestDto.getColor());
        if (requestDto.getLatitude() != null) pin.setLatitude(requestDto.getLatitude());
        if (requestDto.getLongitude() != null) pin.setLongitude(requestDto.getLongitude());
        if (requestDto.getPinOrder() != null) pin.setPinOrder(requestDto.getPinOrder());
        if (requestDto.getUserId() != null) pin.setUserId(requestDto.getUserId()); // DTO에 해당 필드가 있다고 가정
        if (requestDto.getProvinceId() != null) pin.setProvinceId(requestDto.getProvinceId()); // DTO에 해당 필드가 있다고 가정
        if (requestDto.getCityId() != null) pin.setCityId(requestDto.getCityId()); // DTO에 해당 필드가 있다고 가정
        if (requestDto.getTownId() != null) pin.setTownId(requestDto.getTownId()); // DTO에 해당 필드가 있다고 가정

        pin.setUpdatedAt(LocalDateTime.now());

        pinRepository.save(pin);
    }

    @Override
    @Transactional
    public void deletePin(Long pinId) {
        Pin pin = pinRepository.findById(pinId)
                .orElseThrow(() -> new GeneralException(PinErrorCode.PIN_NOT_FOUND));

        pin.setDeletedAt(LocalDateTime.now()); // 소프트 삭제
        pinRepository.save(pin);
    }

    @Override
    public MapResponseDTO.PinResponseDto getPinById(Long pinId) {
        Pin pin = pinRepository.findById(pinId)
                .orElseThrow(() -> new GeneralException(PinErrorCode.PIN_NOT_FOUND));

        return fromEntity(pin);
    }

    public static MapResponseDTO.PinResponseDto fromEntity(Pin pin) {
        return MapResponseDTO.PinResponseDto.builder()
                .pinId(pin.getId())
                .courseId(pin.getCourseId()) // Pin 객체에서 courseId 직접 가져오기
                .name(pin.getName())
                .detail(pin.getDetail())
                .color(pin.getColor())
                .latitude(pin.getLatitude())
                .longitude(pin.getLongitude())
                .pinOrder(pin.getPinOrder())
                .userId(pin.getUserId()) // DTO에 해당 필드가 있다고 가정
                .provinceId(pin.getProvinceId()) // DTO에 해당 필드가 있다고 가정
                .cityId(pin.getCityId()) // DTO에 해당 필드가 있다고 가정
                .townId(pin.getTownId()) // DTO에 해당 필드가 있다고 가정
                .build();
    }
}