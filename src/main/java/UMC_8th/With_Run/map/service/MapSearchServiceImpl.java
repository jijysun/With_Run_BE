package UMC_8th.With_Run.map.service;


import UMC_8th.With_Run.map.dto.MapRequestDTO;
import UMC_8th.With_Run.map.dto.MapResponseDTO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

import java.net.URLEncoder;

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

    @PostConstruct
    public void init() {
        System.out.println("▶ clientId: " + clientId);
        System.out.println("▶ clientSecret: " + clientSecret);
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

            // 🔍 로그 확인용 추가
            System.out.println("▶ headers = X-Naver-Client-Id: " + clientId.trim() + ", X-Naver-Client-Secret: " + clientSecret.trim());
            System.out.println("▶ uri = " + uri.toString());

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


    /**
     * 키워드 검색 (동적/정적 통합)
     */
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

            // 🔍 로그 확인용 추가
            System.out.println("▶ headers = X-Naver-Client-Id: " + clientId.trim() + ", X-Naver-Client-Secret: " + clientSecret.trim());
            System.out.println("▶ uri = " + uri.toString());

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
            String encoded = URLEncoder.encode(placeName, StandardCharsets.UTF_8);
            URI uri = UriComponentsBuilder
                    .fromUriString("https://openapi.naver.com")
                    .path("/v1/search/local.json")
                    .queryParam("query", encoded)
                    .queryParam("display", 1)
                    .queryParam("start", 1)
                    .encode()
                    .build()
                    .toUri();

            // 🔍 로그 확인용 추가
            System.out.println("▶ headers = X-Naver-Client-Id: " + clientId.trim() + ", X-Naver-Client-Secret: " + clientSecret.trim());
            System.out.println("▶ uri = " + uri.toString());

            RequestEntity<Void> request = RequestEntity.get(uri)
                    .header("X-Naver-Client-Id", clientId.trim())
                    .header("X-Naver-Client-Secret", clientSecret.trim())
                    .build();

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.exchange(request, String.class);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode item = mapper.readTree(response.getBody()).path("items").get(0);

            if (item == null) return null;

            return MapResponseDTO.PlaceResponseDto.builder()
                    .id(null)
                    .name(removeHtmlTags(item.path("title").asText()))
                    .address(item.path("address").asText())
                    .latitude(parseOrNull(item.path("mapy").asText()))
                    .longitude(parseOrNull(item.path("mapx").asText()))
                    .imageUrl(null)
                    .openStatus("영업중")
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
