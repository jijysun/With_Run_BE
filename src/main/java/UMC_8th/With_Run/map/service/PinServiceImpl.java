package UMC_8th.With_Run.map.service;

import UMC_8th.With_Run.common.apiResponse.basecode.PinErrorCode;
import UMC_8th.With_Run.common.exception.GeneralException;
import UMC_8th.With_Run.map.dto.MapRequestDTO;
import UMC_8th.With_Run.map.dto.MapResponseDTO;
import UMC_8th.With_Run.map.entity.Pin;
import UMC_8th.With_Run.map.repository.PinRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PinServiceImpl implements PinService {

    private final PinRepository pinRepository;

    @Override
    public void createPin(MapRequestDTO.PinRequestDto requestDto) {
        Pin pin = Pin.builder()
                .courseId(requestDto.getCourseId())
                .name(requestDto.getName())
                .detail(requestDto.getDetail())
                .color(requestDto.getColor())
                .latitude(requestDto.getLatitude())
                .longitude(requestDto.getLongitude())
                .createdAt(LocalDateTime.now())
                .build();

        pinRepository.save(pin);
    }

    @Override
    public void updatePin(Long pinId, MapRequestDTO.PinRequestDto requestDto) {
        Pin pin = pinRepository.findById(pinId)
                .orElseThrow(() -> new IllegalArgumentException("해당 핀 없음"));
        pin.setCourseId(requestDto.getCourseId());
        pin.setName(requestDto.getName());
        pin.setDetail(requestDto.getDetail());
        pin.setColor(requestDto.getColor());
        pin.setLatitude(requestDto.getLatitude());
        pin.setLongitude(requestDto.getLongitude());
        pin.setUpdatedAt(LocalDateTime.now());

        pinRepository.save(pin);
    }

    @Override
    @Transactional // 데이터 변경 작업이므로 @Transactional 추가
    public void deletePin(Long pinId) {
        Pin pin = pinRepository.findById(pinId)
                // IllegalArgumentException 대신 GeneralException 사용
                .orElseThrow(() -> new GeneralException(PinErrorCode.PIN_NOT_FOUND)); // 또는 new GeneralException("PIN_NOT_FOUND", "해당 핀을 찾을 수 없습니다.");

        pin.setDeletedAt(LocalDateTime.now());
        pinRepository.save(pin);
    }

    @Override
    public MapResponseDTO.PinResponseDto getPinById(Long pinId) {
        Pin pin = pinRepository.findById(pinId)
                .orElseThrow(() -> new IllegalArgumentException("해당 핀 없음"));
        return fromEntity(pin);
    }

    public static MapResponseDTO.PinResponseDto fromEntity(Pin pin) {
        return MapResponseDTO.PinResponseDto.builder()
                .pinId(pin.getId())
                .courseId(pin.getCourseId())
                .name(pin.getName())
                .detail(pin.getDetail())
                .color(pin.getColor())
                .latitude(pin.getLatitude())
                .longitude(pin.getLongitude())
                .build();
    }
}

