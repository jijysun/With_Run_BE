package UMC_8th.With_Run.map.service;

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
    @Transactional
    public Long createPin(MapRequestDTO.PinRequestDto requestDto) {
        Pin pin = Pin.builder()
                .name(requestDto.getName())
                .detail(requestDto.getDetail())
                .color(requestDto.getColor())
                .latitude(requestDto.getLatitude())
                .longitude(requestDto.getLongitude())
                .createdAt(LocalDateTime.now())
                .build();

        pinRepository.save(pin);

        return pin.getId();
    }

    @Override
    @Transactional
    public Long updatePin(Long pinId, MapRequestDTO.PinRequestDto requestDto) {
        Pin pin = pinRepository.findById(pinId)
                .orElseThrow(() -> new IllegalArgumentException("해당 핀 없음"));
        pin.setName(requestDto.getName());
        pin.setDetail(requestDto.getDetail());
        pin.setColor(requestDto.getColor());
        pin.setLatitude(requestDto.getLatitude());
        pin.setLongitude(requestDto.getLongitude());
        pin.setUpdatedAt(LocalDateTime.now());

        pinRepository.save(pin);
        return pin.getId();
    }

    @Override
    @Transactional
    public Long deletePin(Long pinId) { // void -> Long 으로 반환 타입 수정
        // pin 변수를 사용하기 위해 주석을 제거하고 Pin 객체를 조회합니다.
        Pin pin = pinRepository.findById(pinId)
                .orElseThrow(() -> new IllegalArgumentException("해당 핀 없음"));

        pinRepository.deleteById(pinId); // 물리적 삭제

        return pin.getId(); // 삭제된 핀의 ID를 반환
    }


    @Override
    @Transactional(readOnly = true) // 데이터 조회만 하므로 readOnly=true 설정
    public MapResponseDTO.GetPinDto getPinById(Long pinId) {
        Pin pin = pinRepository.findById(pinId)
                .orElseThrow(() -> new IllegalArgumentException("해당 핀 없음"));
        return fromEntity(pin);
    }

    public static MapResponseDTO.GetPinDto fromEntity(Pin pin) {
        return MapResponseDTO.GetPinDto.builder()
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