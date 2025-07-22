package UMC_8th.With_Run.map.service;

import UMC_8th.With_Run.map.dto.*;
import UMC_8th.With_Run.map.entity.Pin;
import UMC_8th.With_Run.map.repository.PinRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PinServiceImpl implements PinService {

    private final PinRepository pinRepository;

    @Override
    public MapResponseDTO.PinResponseDto createPin(MapRequestDTO.PinRequestDto requestDto) {
        Pin pin = Pin.builder()
                .userId(requestDto.getUserId())
                .name(requestDto.getName())
                .detail(requestDto.getDetail())
                .color(requestDto.getColor())
                .latitude(requestDto.getLatitude())
                .longitude(requestDto.getLongitude())
                .createdAt(LocalDateTime.now())
                .build();

        Pin saved = pinRepository.save(pin);
        return fromEntity(saved);
    }

    @Override
    public MapResponseDTO.PinResponseDto updatePin(Long pinId, MapRequestDTO.PinRequestDto requestDto) {
        Pin pin = pinRepository.findById(pinId)
                .orElseThrow(() -> new IllegalArgumentException("해당 핀 없음"));
        pin.setUserId(requestDto.getUserId());
        pin.setName(requestDto.getName());
        pin.setDetail(requestDto.getDetail());
        pin.setColor(requestDto.getColor());
        pin.setLatitude(requestDto.getLatitude());
        pin.setLongitude(requestDto.getLongitude());
        pin.setUpdatedAt(LocalDateTime.now());
        Pin updated = pinRepository.save(pin);
        return fromEntity(updated);
    }

    @Override
    public void deletePin(Long pinId) {
        Pin pin = pinRepository.findById(pinId)
                .orElseThrow(() -> new IllegalArgumentException("해당 핀 없음"));
        pin.setDeletedAt(LocalDateTime.now());
        pinRepository.save(pin);
    }

    public static MapResponseDTO.PinResponseDto fromEntity(Pin pin) {
        return MapResponseDTO.PinResponseDto.builder()
                .pinId(pin.getId())
                .userId(pin.getUserId())
                .name(pin.getName())
                .detail(pin.getDetail())
                .color(pin.getColor())
                .latitude(pin.getLatitude())
                .longitude(pin.getLongitude())
                .build();
    }
}
