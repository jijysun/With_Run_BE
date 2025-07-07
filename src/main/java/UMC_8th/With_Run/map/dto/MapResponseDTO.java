package UMC_8th.With_Run.map.dto;

import lombok.*;

public class MapResponseDTO {


    @Builder @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class CourseCreateResponseDto {
        private Long courseId;
        private String message;
    }

    @Builder @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class PinResponseDto {
        private Long pinId;
        private String message;
    }

    @Builder @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class PetFacilityResponseDto {
        private Long id;
        private String name;
        private String imageUrl;
        private String openingHours;
        private Boolean hasParking;
    }

    @Builder @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class PlaceResponseDto {
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


}
