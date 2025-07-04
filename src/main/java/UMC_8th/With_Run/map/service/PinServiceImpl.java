package UMC_8th.With_Run.map.service;

import UMC_8th.With_Run.map.dto.PinRequestDto;
import UMC_8th.With_Run.map.dto.PinResponseDto;
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
    public PinResponseDto createPin(PinRequestDto requestDto) {
        Pin pin = new Pin();
        pin.setName(requestDto.getName());
        pin.setDetail(requestDto.getDetail());
        pin.setColor(requestDto.getColor());
        pin.setCreatedAt(LocalDateTime.now());

        Pin saved = pinRepository.save(pin);
        return new PinResponseDto(saved.getId(), "핀 생성 완료");
    }

    @Override
    public PinResponseDto updatePin(Long pinId, PinRequestDto requestDto) {
        Pin pin = pinRepository.findById(pinId)
                .orElseThrow(() -> new IllegalArgumentException("해당 핀 없음"));

        pin.setName(requestDto.getName());
        pin.setDetail(requestDto.getDetail());
        pin.setColor(requestDto.getColor());
        pin.setUpdatedAt(LocalDateTime.now());

        pinRepository.save(pin);
        return new PinResponseDto(pin.getId(), "핀 수정 완료");
    }

    @Override
    public PinResponseDto deletePin(Long pinId) {
        Pin pin = pinRepository.findById(pinId)
                .orElseThrow(() -> new IllegalArgumentException("해당 핀 없음"));

        pin.setDeletedAt(LocalDateTime.now());
        pinRepository.save(pin);
        return new PinResponseDto(pin.getId(), "핀 삭제 완료");
    }
}
