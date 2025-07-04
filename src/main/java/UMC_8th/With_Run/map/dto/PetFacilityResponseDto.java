package UMC_8th.With_Run.map.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PetFacilityResponseDto {
    private Long id;
    private String name;
    private String imageUrl;
    private String openingHours;
    private Boolean hasParking;
}
