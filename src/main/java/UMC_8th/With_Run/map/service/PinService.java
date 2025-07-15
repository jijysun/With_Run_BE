package UMC_8th.With_Run.map.service;

import UMC_8th.With_Run.map.dto.*;

public interface PinService {
    MapResponseDTO.PinResponseDto createPin(MapRequestDTO.PinRequestDto requestDto);
    MapResponseDTO.PinResponseDto updatePin(Long pinId, MapRequestDTO.PinRequestDto requestDto);
    MapResponseDTO.PinResponseDto deletePin(Long pinId);
}
