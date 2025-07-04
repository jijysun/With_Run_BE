package UMC_8th.With_Run.map.service;

import UMC_8th.With_Run.map.dto.PetFacilityResponseDto;
import UMC_8th.With_Run.map.dto.PlaceResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MapSearchServiceImpl implements MapSearchService {

    @Override
    public List<PlaceResponseDto> searchPlacesByCategory(String category) {
        List<PlaceResponseDto> results = new ArrayList<>();

        if (category.contains("약국")) {
            results.add(PlaceResponseDto.builder()
                    .id(1L)
                    .name("연남약국")
                    .address("서울 마포구 연남로 11길 14")
                    .latitude(37.562541)
                    .longitude(126.925011)
                    .imageUrl("https://example.com/images/dogcafe.jpg")
                    .openStatus("영업중")
                    .openingHours("10:00~21:00")
                    .parkingAvailable(true)
                    .build());

            results.add(PlaceResponseDto.builder()
                    .id(2L)
                    .name("별빛약국")
                    .address("서울 마포구 연남로 11길 14")
                    .latitude(37.562541)
                    .longitude(126.925011)
                    .imageUrl("https://example.com/images/dogcafe.jpg")
                    .openStatus("영업중")
                    .openingHours("9:00~20:30")
                    .parkingAvailable(true)
                    .build());
        }

        return results;
    }

    @Override
    public List<PlaceResponseDto> searchPlacesByKeyword(String keyword) {
        List<PlaceResponseDto> results = new ArrayList<>();

        if (keyword.contains("약국")) {
            results.add(PlaceResponseDto.builder()
                    .id(1L)
                    .name("연남약국")
                    .address("서울 마포구 연남로 11길 14")
                    .latitude(37.562541)
                    .longitude(126.925011)
                    .imageUrl("https://example.com/images/dogcafe.jpg")
                    .openStatus("영업중")
                    .openingHours("10:00~21:00")
                    .parkingAvailable(true)
                    .build());

            results.add(PlaceResponseDto.builder()
                    .id(2L)
                    .name("별빛약국")
                    .address("서울 마포구 연남로 11길 14")
                    .latitude(37.562541)
                    .longitude(126.925011)
                    .imageUrl("https://example.com/images/dogcafe.jpg")
                    .openStatus("영업중")
                    .openingHours("9:00~20:30")
                    .parkingAvailable(true)
                    .build());
        }

        return results;
    }

    @Override
    public PlaceResponseDto getPlaceById(Long placeId) {
        // 임시 하드코딩
        if (placeId == 1L) {
            return PlaceResponseDto.builder()
                    .id(1L)
                    .name("연남약국")
                    .address("서울 마포구 연남로 11길 14")
                    .latitude(37.562541)
                    .longitude(126.925011)
                    .imageUrl("https://example.com/images/dogcafe.jpg")
                    .openStatus("영업중")
                    .openingHours("10:00~21:00")
                    .parkingAvailable(true)
                    .build();
        } else if (placeId == 2L) {
            return PlaceResponseDto.builder()
                    .id(2L)
                    .name("별빛약국")
                    .address("서울 마포구 연남로 11길 14")
                    .latitude(37.562541)
                    .longitude(126.925011)
                    .imageUrl("https://example.com/images/dogcafe.jpg")
                    .openStatus("영업중")
                    .openingHours("9:00~20:30")
                    .parkingAvailable(true)
                    .build();
        }

        // 예외 처리 (임시)
        throw new IllegalArgumentException("존재하지 않는 placeId: " + placeId);
    }

    @Override
    public List<PetFacilityResponseDto> getAllPetFacilities() {
        List<PetFacilityResponseDto> results = new ArrayList<>();

        results.add(PetFacilityResponseDto.builder()
                .id(1L)
                .name("카페 도그라운드")
                .imageUrl("https://example.com/images/dogcafe.jpg")
                .openingHours("10:00~21:00")
                .hasParking(true)
                .build());

        results.add(PetFacilityResponseDto.builder()
                .id(2L)
                .name("홍대 펫약국")
                .imageUrl("https://example.com/images/petpharmacy.jpg")
                .openingHours("09:00~20:00")
                .hasParking(false)
                .build());

        return results;
    }


}


