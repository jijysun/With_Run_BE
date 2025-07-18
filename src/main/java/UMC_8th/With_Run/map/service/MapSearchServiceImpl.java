package UMC_8th.With_Run.map.service;

import UMC_8th.With_Run.map.dto.*;
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
    private String NAVER_API_ID;

    @Value("${naver.secret}")
    private String NAVER_API_SECRET;

    /**
     * 키워드 검색 (동적/정적 통합)
     */
    @Override
    public List<MapResponseDTO.PlaceResponseDto> searchPlacesByKeyword(String keyword) {
        return searchFromNaver(keyword);
    }

    /**
     * 카테고리 검색 (ex. 약국, 동물병원 등)
     */
    @Override
    public List<MapResponseDTO.PlaceResponseDto> searchPlacesByCategory(String category) {
        return searchFromNaver(category); // 내부 로직 동일, query만 다를 뿐
    }

    /**
     * 장소 ID로 상세 정보 조회 (현재는 네이버 API에 고유 ID가 없으므로 임시 구현)
     */
    @Override
    public MapResponseDTO.PlaceResponseDto getPlaceById(Long placeId) {
        // 임시 방식: '약국'으로 검색해서 index 기반으로 placeId 매칭
        List<MapResponseDTO.PlaceResponseDto> places = searchFromNaver("약국");

        return places.stream()
                .filter(p -> placeId.equals(p.getId()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 placeId: " + placeId));
    }

    /**
     * 네이버 API 공통 호출 로직
     */
    private List<MapResponseDTO.PlaceResponseDto> searchFromNaver(String query) {
        List<MapResponseDTO.PlaceResponseDto> results = new ArrayList<>();

        try {
            // UTF-8로 인코딩된 검색어 생성
            ByteBuffer buffer = StandardCharsets.UTF_8.encode(query);
            String encode = StandardCharsets.UTF_8.decode(buffer).toString();

            // 네이버 검색 API를 호출하기 위한 URI 생성
            URI uri = UriComponentsBuilder.fromUriString("https://openapi.naver.com")
                    .path("/v1/search/local.json")
                    .queryParam("query", encode)
                    .queryParam("display", 10)
                    .queryParam("start", 1)
                    .queryParam("sort", "random")
                    .build()
                    .toUri();

            // RestTemplate을 사용하여 네이버 API에 요청을 보냄
            RestTemplate restTemplate = new RestTemplate();
            RequestEntity<Void> req = RequestEntity.get(uri)
                    .header("X-Naver-Client-Id", NAVER_API_ID)
                    .header("X-Naver-Client-Secret", NAVER_API_SECRET)
                    .build();

            // API 응답 데이터를 JSON 형식으로 변환
            ResponseEntity<String> response = restTemplate.exchange(req, String.class);
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode items = objectMapper.readTree(response.getBody()).path("items");

            long idCounter = 1L;
            for (JsonNode item : items) {
                //위도와 경도를 double 형식으로 변환하여 저장
                double lat = Double.parseDouble(item.path("mapy").asText()) / 1e7;
                double lng = Double.parseDouble(item.path("mapx").asText()) / 1e7;

                MapResponseDTO.PlaceResponseDto place = MapResponseDTO.PlaceResponseDto.builder()
                        .id(idCounter++) // 네이버는 id가 없어서 가상 id 부여
                        .name(item.path("title").asText().replaceAll("<.*?>", ""))
                        .address(item.path("address").asText())
                        .latitude(lat)
                        .longitude(lng)
                        .imageUrl(null)
                        .openStatus(null)
                        .openingHours(null)
                        .parkingAvailable(null)
                        .build();

                results.add(place);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return results;
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
        // 1. 토큰 검증 (JWT 토큰 검증 또는 유저 조회 등)
        // 2. Course entity 저장
        // 3. Pins와 매핑 저장
        // 4. 지역, 키워드 등 저장

        // 임시 courseId 반환
        return 123L;
    }

}


