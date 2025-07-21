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

    private String removeHtmlTags(String input) {
        if (input == null) return null;
        return input.replaceAll("<[^>]*>", "");
    }

    // getDetailedPlaceInfo는 place_id로 상세 정보를 가져오므로 변경 없음
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

            String status = result.path("business_status").asText();
            String openStatus = switch (status) {
                case "OPERATIONAL" -> "영업중";
                case "CLOSED_TEMPORARILY" -> "임시휴업";
                case "CLOSED_PERMANENTLY" -> "폐업";
                default -> "정보 없음";
            };

            JsonNode hoursNode = result.path("opening_hours").path("weekday_text");
            String openingHours = null;
            if (hoursNode.isArray() && hoursNode.size() > 0) {
                openingHours = StreamSupport.stream(hoursNode.spliterator(), false)
                        .map(JsonNode::asText)
                        .collect(Collectors.joining(", "));
            } else {
                openingHours = "정보 없음";
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
                    .openStatus(openStatus)
                    .openingHours(openingHours)
                    .parkingAvailable(hasParking)
                    .build();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    @Override
    // ⭐ lat, lng 파라미터 제거 ⭐
    public List<MapResponseDTO.PlaceResponseDto> searchPlacesByCategory(String category) {
        List<MapResponseDTO.PlaceResponseDto> resultList = new ArrayList<>();

        try {
            // Google Places Text Search API 사용
            // location, radius 파라미터 제거 (현 위치 기반 검색이 아닌 일반 텍스트 검색)
            URI uri = UriComponentsBuilder
                    .fromUriString("https://maps.googleapis.com/maps/api/place/textsearch/json")
                    .queryParam("query", category)
                    // .queryParam("location", lat + "," + lng) // ⭐ 이 줄 제거 ⭐
                    // .queryParam("radius", 5000) // ⭐ 이 줄 제거 ⭐
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
    // ⭐ lat, lng 파라미터 제거 ⭐
    public List<MapResponseDTO.PlaceResponseDto> searchPlacesByKeyword(String query) {
        List<MapResponseDTO.PlaceResponseDto> resultList = new ArrayList<>();

        try {
            // Google Places Text Search API 사용
            // location, radius 파라미터 제거 (현 위치 기반 검색이 아닌 일반 텍스트 검색)
            URI uri = UriComponentsBuilder
                    .fromUriString("https://maps.googleapis.com/maps/api/place/textsearch/json")
                    .queryParam("query", query)
                    // .queryParam("location", lat + "," + lng) // ⭐ 이 줄 제거 ⭐
                    // .queryParam("radius", 5000) // ⭐ 이 줄 제거 ⭐
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
        // 이 공용 메서드는 내부 헬퍼 메서드를 호출하여 상세 정보를 반환합니다.
        return getDetailedPlaceInfo(placeId);
    }

    // ⭐ MapController에서 getPlaceDetailByName을 호출하므로, 이 메서드를 재구현해야 합니다. ⭐
    @Override
    public MapResponseDTO.PlaceResponseDto getPlaceDetailByName(String placeName) {
        try {
            // Google Places API는 이름으로 상세 조회를 직접 지원하지 않으므로,
            // 먼저 Text Search를 통해 placeId를 얻은 후, 해당 placeId로 상세 정보를 다시 요청해야 합니다.
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
                return getDetailedPlaceInfo(placeId); // 첫 번째 결과의 상세 정보 반환
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null; // 결과를 찾지 못했거나 오류 발생
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
    public Long createCourse(String accessToken, MapRequestDTO.CourseCreateRequestDto dto) {
        return 123L;
    }
}