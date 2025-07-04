package UMC_8th.With_Run.map.service;

import UMC_8th.With_Run.map.dto.PinRequestDto;
import UMC_8th.With_Run.map.dto.PinResponseDto;

public interface PinService {
    PinResponseDto createPin(PinRequestDto requestDto);
    PinResponseDto updatePin(Long pinId, PinRequestDto requestDto);
    PinResponseDto deletePin(Long pinId);
}
