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
    @Transactional
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
    @Transactional
    public void deletePin(Long pinId) {
        // 수정 부분 시작: 소프트 딜리트 -> 하드 딜리트
        // Pin pin = pinRepository.findById(pinId)
        //         .orElseThrow(() -> new IllegalArgumentException("해당 핀 없음"));
        // pin.setDeletedAt(LocalDateTime.now());
        // pinRepository.save(pin);

        // 하드 딜리트: ID로 직접 삭제
        if (!pinRepository.existsById(pinId)) { // 핀이 존재하는지 먼저 확인
            throw new IllegalArgumentException("해당 핀 없음");
        }
        pinRepository.deleteById(pinId); // 물리적 삭제
        // 수정 부분 끝
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

