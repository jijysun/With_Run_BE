package UMC_8th.With_Run.map.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PlaceResponseDto {
    private Long id;
    private String name;
    private String address;
    private Double latitude;
    private Double longitude;
    private String imageUrl;
    private String openStatus;
    private String openingHours;
    private Boolean parkingAvailable;
}



