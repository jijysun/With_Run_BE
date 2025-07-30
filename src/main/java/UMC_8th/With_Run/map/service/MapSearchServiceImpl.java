package UMC_8th.With_Run.map.service;

import UMC_8th.With_Run.map.dto.MapRequestDTO;
import UMC_8th.With_Run.map.dto.MapResponseDTO;
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

    @Value("${google.places.key}")
    private String apiKey;

    private final CourseService courseService; // CourseService 주입

    private String removeHtmlTags(String input) {
        if (input == null) return null;
        return input.replaceAll("<[^>]*>", "");
    }

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

            String businessStatus = result.path("business_status").asText();
            String openStatusText = switch (businessStatus) {
                case "OPERATIONAL" -> "영업중";
                case "CLOSED_TEMPORARILY" -> "임시휴업";
                case "CLOSED_PERMANENTLY" -> "폐업";
                default -> "정보 없음";
            };

            String openingHours = "정보 없음";
            String currentOperatingStatus = "정보 없음";

            JsonNode openingHoursNode = result.path("opening_hours");
            if (!openingHoursNode.isMissingNode()) {
                JsonNode weekdayTextNode = openingHoursNode.path("weekday_text");
                if (weekdayTextNode.isArray() && weekdayTextNode.size() > 0) {
                    openingHours = StreamSupport.stream(weekdayTextNode.spliterator(), false)
                            .map(JsonNode::asText)
                            .collect(Collectors.joining(", "));
                }

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
                    .openingHours(openingHours)
                    .parkingAvailable(hasParking)
                    .currentOperatingStatus(currentOperatingStatus)
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
        List<MapResponseDTO.PetFacilityResponseDto> results = new ArrayList<>();

        results.add(MapResponseDTO.PetFacilityResponseDto.builder()
                .id(1L)
                .name("카페 도그라운드")
                .imageUrl("https://example.com/images/dogcafe.jpg")
                .openingHours("10:00~21:00")
                .hasParking(true)
                .build());

        results.add(MapResponseDTO.PetFacilityResponseDto.builder()
                .id(2L)
                .name("홍대 펫약국")
                .imageUrl("https://example.com/images/petpharmacy.jpg")
                .openingHours("09:00~20:00")
                .hasParking(false)
                .build());

        return results;
    }

    @Override
    // 수정 부분 시작: createCourse 메소드 호출 시 userId 추가
    public Long createCourse(MapRequestDTO.CourseCreateRequestDto requestDto) {
        // CourseService의 createCourse를 호출하여 실제 코스 생성 로직 실행
        // requestDto에서 userId를 가져와서 전달
        return courseService.createCourse(requestDto.getUserId(), requestDto);
    }
    // 수정 부분 끝
}