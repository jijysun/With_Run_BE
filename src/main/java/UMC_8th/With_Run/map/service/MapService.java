package UMC_8th.With_Run.map.service;

import UMC_8th.With_Run.map.dto.MapRequestDTO;
import UMC_8th.With_Run.map.dto.MapResponseDTO;
import UMC_8th.With_Run.map.entity.PetFacility;
import org.springframework.data.domain.Page;

import java.util.List;

public interface MapService {

    MapResponseDTO.PetFacilityPageResponseDto getPetFacilityByCategory(String category, int page, int size);
    MapResponseDTO.PetFacilityResponseDto getPetFacilityById(Long id);

    Long createPin(MapRequestDTO.PinRequestDto requestDto);
    Long updatePin(Long pinId, MapRequestDTO.PinRequestDto requestDto);
    Long deletePin(Long pinId);
    MapResponseDTO.GetPinDto getPinById(Long pinId);
}
