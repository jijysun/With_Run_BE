package UMC_8th.With_Run.map.service;

import UMC_8th.With_Run.map.dto.*;

public interface PinService {
    Long createPin(MapRequestDTO.PinRequestDto requestDto);
    Long updatePin(Long pinId, MapRequestDTO.PinRequestDto requestDto);
    Long deletePin(Long pinId);
    MapResponseDTO.GetPinDto getPinById(Long pinId);
}
