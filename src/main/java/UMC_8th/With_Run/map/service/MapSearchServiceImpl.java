package UMC_8th.With_Run.map.service;

import UMC_8th.With_Run.map.dto.MapRequestDTO;
import UMC_8th.With_Run.map.dto.MapResponseDTO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MapSearchServiceImpl implements MapSearchService {

    @Value("${naver.client-id}")
    private String clientId;

    @Value("${naver.secret}")
    private String clientSecret;

    private String removeHtmlTags(String input) {
        return input.replaceAll("<[^>]*>", "");
    }

    private Double parseOrNull(String value) {
        try {
            return Double.parseDouble(value) / 1e7;
        } catch (NumberFormatException e) {
            return null;
        }
    }


    @Override
    public List<MapResponseDTO.PlaceResponseDto> searchPlacesByCategory(String category) {
        List<MapResponseDTO.PlaceResponseDto> results = new ArrayList<>();

        try {
            ByteBuffer buffer = StandardCharsets.UTF_8.encode(category);
            String encodedCategory = StandardCharsets.UTF_8.decode(buffer).toString();

            URI uri = UriComponentsBuilder
                    .fromUriString("https://openapi.naver.com")
                    .path("/v1/search/local.json")
                    .queryParam("query", encodedCategory)
                    .queryParam("display", 10)
                    .queryParam("start", 1)
                    .queryParam("sort", "random")
                    .encode()
                    .build()
                    .toUri();


            RequestEntity<Void> request = RequestEntity.get(uri)
                    .header("X-Naver-Client-Id", clientId.trim())
                    .header("X-Naver-Client-Secret", clientSecret.trim())
                    .build();

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.exchange(request, String.class);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode items = mapper.readTree(response.getBody()).path("items");

            for (JsonNode item : items) {
                MapResponseDTO.PlaceResponseDto dto = MapResponseDTO.PlaceResponseDto.builder()
                        .id(null)
                        .name(removeHtmlTags(item.path("title").asText()))
                        .address(item.path("address").asText())
                        .latitude(parseOrNull(item.path("mapy").asText()))
                        .longitude(parseOrNull(item.path("mapx").asText()))
                        .imageUrl(null)
                        .openStatus(null)
                        .openingHours(null)
                        .parkingAvailable(null)
                        .build();

                results.add(dto);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return results;
    }

    @Override
    public List<MapResponseDTO.PlaceResponseDto> searchPlacesByKeyword(String query) {
        List<MapResponseDTO.PlaceResponseDto> resultList = new ArrayList<>();

        try {
            ByteBuffer buffer = StandardCharsets.UTF_8.encode(query);
            String encodedQuery = StandardCharsets.UTF_8.decode(buffer).toString();

            URI uri = UriComponentsBuilder
                    .fromUriString("https://openapi.naver.com")
                    .path("/v1/search/local.json")
                    .queryParam("query", encodedQuery)
                    .queryParam("display", 10)
                    .queryParam("start", 1)
                    .queryParam("sort", "random")
                    .encode()
                    .build()
                    .toUri();


            RequestEntity<Void> request = RequestEntity.get(uri)
                    .header("X-Naver-Client-Id", clientId.trim())
                    .header("X-Naver-Client-Secret", clientSecret.trim())
                    .build();

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.exchange(request, String.class);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode items = mapper.readTree(response.getBody()).path("items");

            for (JsonNode item : items) {
                MapResponseDTO.PlaceResponseDto dto = MapResponseDTO.PlaceResponseDto.builder()
                        .id(null)
                        .name(removeHtmlTags(item.path("title").asText()))
                        .address(item.path("address").asText())
                        .latitude(parseOrNull(item.path("mapy").asText()))
                        .longitude(parseOrNull(item.path("mapx").asText()))
                        .imageUrl(null)
                        .openStatus(null)
                        .openingHours(null)
                        .parkingAvailable(null)
                        .build();

                resultList.add(dto);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return resultList;
    }


    @Override
    public MapResponseDTO.PlaceResponseDto getPlaceDetailByName(String placeName) {
        try {
            URI uri = UriComponentsBuilder
                    .fromUriString("https://openapi.naver.com")
                    .path("/v1/search/local.json")
                    .queryParam("query", placeName)  // 인코딩은 UriComponentsBuilder에 맡김
                    .queryParam("display", 1)
                    .queryParam("start", 1)
                    .encode()
                    .build()
                    .toUri();

            System.out.println("▶ 요청 URI: " + uri);

            RequestEntity<Void> request = RequestEntity.get(uri)
                    .header("X-Naver-Client-Id", clientId.trim())
                    .header("X-Naver-Client-Secret", clientSecret.trim())
                    .build();

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.exchange(request, String.class);

            System.out.println("▶ 네이버 API 응답: " + response.getBody());

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.getBody());
            JsonNode items = root.path("items");

            if (items.isEmpty()) {
                System.out.println("items 배열이 비어있음");
                return null;
            }

            JsonNode item = items.get(0);
            if (item == null) {
                System.out.println("첫 번째 아이템이 null임");
                return null;
            }

            return MapResponseDTO.PlaceResponseDto.builder()
                    .id(null)
                    .name(removeHtmlTags(item.path("title").asText()))
                    .address(item.path("address").asText())
                    .latitude(parseOrNull(item.path("mapy").asText()))
                    .longitude(parseOrNull(item.path("mapx").asText()))
                    .imageUrl(null)
                    .openStatus("영업중")  // 계산코드추가하기
                    .openingHours("09:00 ~ 18:00")
                    .parkingAvailable(null)
                    .build();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
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
