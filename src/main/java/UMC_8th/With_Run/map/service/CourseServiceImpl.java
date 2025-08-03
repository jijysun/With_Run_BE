package UMC_8th.With_Run.map.service;

import UMC_8th.With_Run.common.apiResponse.status.ErrorCode;
import UMC_8th.With_Run.common.exception.handler.MapHandler;
import UMC_8th.With_Run.course.entity.Course;
import UMC_8th.With_Run.course.repository.CourseRepository;
import UMC_8th.With_Run.map.dto.MapRequestDTO;
import UMC_8th.With_Run.map.entity.Pin;
import UMC_8th.With_Run.map.entity.RegionProvince;
import UMC_8th.With_Run.map.entity.RegionsCity;
import UMC_8th.With_Run.map.repository.PinRepository;
import UMC_8th.With_Run.user.repository.RegionProvinceRepository;
import UMC_8th.With_Run.map.repository.RegionsCityRepository;

import UMC_8th.With_Run.user.entity.User;
import UMC_8th.With_Run.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final PinRepository pinRepository;

    private final RegionProvinceRepository regionProvinceRepository;
    private final RegionsCityRepository regionsCityRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public Long createCourse(MapRequestDTO.CourseCreateRequestDto requestDto) {
        return createCourse(requestDto.getUserId(), requestDto);
    }

    @Override
    @Transactional
    public Long createCourse(Long userId, MapRequestDTO.CourseCreateRequestDto requestDto) {
        List<Long> pinIds = requestDto.getPinIds();

        List<Pin> foundPins = pinRepository.findAllById(pinIds);

        if (foundPins.size() != pinIds.size()) {
            throw new MapHandler(ErrorCode.PIN_NOT_FOUND);
        }

        String keywordsString = String.join(",", requestDto.getKeyWords());
        int time = requestDto.getTime();

        // DTO에서 userId를 가져와 User 엔티티를 조회
        User user = userRepository.findById(requestDto.getUserId())
                .orElseThrow(() -> new MapHandler(ErrorCode.USER_NOT_FOUND));


        // 1. DTO에서 받은 ID로 RegionProvince 엔티티를 조회
        RegionProvince regionProvince = regionProvinceRepository.findById(requestDto.getRegionProvinceId())
                .orElseThrow(() -> new MapHandler(ErrorCode.REGION_PROVINCE_NOT_FOUND));

        // 2. DTO에서 받은 ID로 RegionsCity 엔티티를 조회
        RegionsCity regionsCity = regionsCityRepository.findById(requestDto.getRegionsCityId())
                .orElseThrow(() -> new MapHandler(ErrorCode.REGION_CITY_NOT_FOUND));

        // 3. 빌더를 통해 Course 엔티티 생성 시, 조회한 엔티티를 할당
        Course course = Course.builder()
                .name(requestDto.getName())
                .description(requestDto.getDescription())
                .keyWord(keywordsString)

                .time(time)
                .user(user)
                .createdAt(LocalDateTime.now())
                .regionProvince(regionProvince) // 조회한 엔티티 할당
                .regionsCity(regionsCity)
                .overviewPolyline(requestDto.getOverviewPolyline())
                .build();

        courseRepository.save(course);

        Map<Long, Pin> pinMap = foundPins.stream()
                .collect(Collectors.toMap(Pin::getId, pin -> pin));

        for (Long pinId : pinIds) {
            Pin pin = pinMap.get(pinId);

            if (pin == null) {
                throw new MapHandler(ErrorCode.PIN_NOT_FOUND);
            }
            pin.setCourseId(course.getId());
            pin.setUpdatedAt(LocalDateTime.now());
        }

        pinRepository.saveAll(foundPins);

        return course.getId();
    }
}