package UMC_8th.With_Run.map.service;

import UMC_8th.With_Run.map.dto.MapRequestDTO;
import UMC_8th.With_Run.map.dto.MapResponseDTO;
import UMC_8th.With_Run.map.entity.PetFacility;
import UMC_8th.With_Run.map.repository.PetFacilityRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class MapSearchServiceImpl implements MapSearchService {

    private final PetFacilityRepository petFacilityRepository;

    @Value("${google.places.key}")
    private String apiKey;

    private MapResponseDTO.PlaceResponseDto getDetailedPlaceInfo(String placeId) {
        try {
            URI uri = UriComponentsBuilder
                    .fromUriString("https://maps.googleapis.com/maps/api/place/details/json")
                    .queryParam("place_id", placeId)
                    .queryParam("fields", "name,formatted_address,geometry,photos,opening_hours,business_status,types")
                    .queryParam("key", apiKey)
                    .encode()
                    .build()
                    .toUri();

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.getForEntity(uri, String.class);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode result = mapper.readTree(response.getBody()).path("result");

            if (result.isMissingNode() || result.isNull()) {
                return null;
            }

            String name = result.path("name").asText();
            String address = result.path("formatted_address").asText();
            double lat = result.path("geometry").path("location").path("lat").asDouble();
            double lng = result.path("geometry").path("location").path("lng").asDouble();

            // 'business_status'를 이용한 일반 영업 상태
            String businessStatus = result.path("business_status").asText();
            String openStatusText = switch (businessStatus) {
                case "OPERATIONAL" -> "영업중";
                case "CLOSED_TEMPORARILY" -> "임시휴업";
                case "CLOSED_PERMANENTLY" -> "폐업";
                default -> "정보 없음";
            };

            String openingHours = "정보 없음"; // 주간 영업 시간 텍스트
            String currentOperatingStatus = "정보 없음"; // 현재 시각 기준 영업 상태 

            JsonNode openingHoursNode = result.path("opening_hours");
            if (!openingHoursNode.isMissingNode()) {
                // 주간 영업 시간 텍스트 추출
                JsonNode weekdayTextNode = openingHoursNode.path("weekday_text");
                if (weekdayTextNode.isArray() && weekdayTextNode.size() > 0) {
                    openingHours = StreamSupport.stream(weekdayTextNode.spliterator(), false)
                            .map(JsonNode::asText)
                            .collect(Collectors.joining(", "));
                }

                // 'open_now' 필드 값 확인 및 currentOperatingStatus 설정 
                // open_now 필드가 true이면 "영업중", false이면 "영업 종료"
                boolean isOpenNowBool = openingHoursNode.path("open_now").asBoolean(false);
                currentOperatingStatus = isOpenNowBool ? "영업중" : "영업 종료";

            }


            String photoRef = null;
            JsonNode photosNode = result.path("photos");
            if (photosNode.isArray() && photosNode.size() > 0) {
                photoRef = photosNode.get(0).path("photo_reference").asText();
            }

            String imageUrl = (photoRef != null && !photoRef.isEmpty())
                    ? "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photo_reference=" + photoRef + "&key=" + apiKey
                    : null;

            boolean hasParking = false;
            JsonNode types = result.path("types");
            if (types.isArray()) {
                for (JsonNode type : types) {
                    String typeText = type.asText();
                    if (typeText.contains("parking") || typeText.contains("car_park")) {
                        hasParking = true;
                        break;
                    }
                }
            }

            return MapResponseDTO.PlaceResponseDto.builder()
                    .id((long) placeId.hashCode())
                    .name(name)
                    .address(address)
                    .latitude(lat)
                    .longitude(lng)
                    .imageUrl(imageUrl)
                    .openingHours(openingHours) // 주간 영업 시간
                    .parkingAvailable(hasParking)
                    .currentOperatingStatus(currentOperatingStatus) // ⭐ 현재 시각 기준 영업 상태 ⭐
                    .build();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    @Override
    public List<MapResponseDTO.PlaceResponseDto> searchPlacesByCategory(String category) {
        List<MapResponseDTO.PlaceResponseDto> resultList = new ArrayList<>();

        try {
            URI uri = UriComponentsBuilder
                    .fromUriString("https://maps.googleapis.com/maps/api/place/textsearch/json")
                    .queryParam("query", category)
                    .queryParam("key", apiKey)
                    .encode()
                    .build()
                    .toUri();

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.getForEntity(uri, String.class);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode results = mapper.readTree(response.getBody()).path("results");

            for (JsonNode item : results) {
                String placeId = item.path("place_id").asText();
                MapResponseDTO.PlaceResponseDto detailed = getDetailedPlaceInfo(placeId);
                if (detailed != null) {
                    resultList.add(detailed);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return resultList;
    }

    @Override
    public List<MapResponseDTO.PlaceResponseDto> searchPlacesByKeyword(String query) {
        List<MapResponseDTO.PlaceResponseDto> resultList = new ArrayList<>();

        try {
            URI uri = UriComponentsBuilder
                    .fromUriString("https://maps.googleapis.com/maps/api/place/textsearch/json")
                    .queryParam("query", query)
                    .queryParam("key", apiKey)
                    .encode()
                    .build()
                    .toUri();

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.getForEntity(uri, String.class);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode results = mapper.readTree(response.getBody()).path("results");

            for (JsonNode item : results) {
                String placeId = item.path("place_id").asText();
                MapResponseDTO.PlaceResponseDto detailed = getDetailedPlaceInfo(placeId);
                if (detailed != null) {
                    resultList.add(detailed);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return resultList;
    }

    @Override
    public MapResponseDTO.PlaceResponseDto getPlaceDetailByPlaceId(String placeId) {
        return getDetailedPlaceInfo(placeId);
    }

    @Override
    public MapResponseDTO.PlaceResponseDto getPlaceDetailByName(String placeName) {
        try {
            URI searchUri = UriComponentsBuilder
                    .fromUriString("https://maps.googleapis.com/maps/api/place/textsearch/json")
                    .queryParam("query", placeName)
                    .queryParam("key", apiKey)
                    .encode()
                    .build()
                    .toUri();

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> searchResponse = restTemplate.getForEntity(searchUri, String.class);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode searchResults = mapper.readTree(searchResponse.getBody()).path("results");

            if (searchResults.isArray() && searchResults.size() > 0) {
                String placeId = searchResults.get(0).path("place_id").asText();
                return getDetailedPlaceInfo(placeId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    public List<MapResponseDTO.PetFacilityResponseDto> getAllPetFacilities() {
        List<PetFacility> petFacilities = petFacilityRepository.findAll();
        
        return petFacilities.stream()
                .map(facility -> MapResponseDTO.PetFacilityResponseDto.builder()
                        .id(facility.getId())
                        .name(facility.getName())
                        .category(facility.getCategory())
                        .longitude(facility.getLongitude())
                        .latitude(facility.getLatitude())
                        .address(facility.getAddress())
                        .closedDay(facility.getClosedDay())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public Long createCourse(String accessToken, MapRequestDTO.CourseCreateRequestDto dto) {
        return 123L;
    }
}