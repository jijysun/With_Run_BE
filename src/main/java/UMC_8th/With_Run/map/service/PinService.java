package UMC_8th.With_Run.map.service;

import UMC_8th.With_Run.map.dto.*;

public interface PinService {
    void createPin(MapRequestDTO.PinRequestDto requestDto);
    void updatePin(Long pinId, MapRequestDTO.PinRequestDto requestDto);
    void deletePin(Long pinId);
    MapResponseDTO.PinResponseDto getPinById(Long pinId);
}
