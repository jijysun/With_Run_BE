package UMC_8th.With_Run.user.service;

import UMC_8th.With_Run.common.apiResponse.status.ErrorStatus;
import UMC_8th.With_Run.common.exception.GeneralException;
import UMC_8th.With_Run.common.security.jwt.JwtTokenProvider;
import UMC_8th.With_Run.course.entity.Course;
import UMC_8th.With_Run.course.repository.CourseRepository;
import UMC_8th.With_Run.map.entity.Pin;
import UMC_8th.With_Run.map.entity.RegionProvince;
import UMC_8th.With_Run.map.entity.RegionsCity;
import UMC_8th.With_Run.map.entity.RegionsTown;
import UMC_8th.With_Run.map.repository.PinRepository;
import UMC_8th.With_Run.user.dto.UserRequestDto;
import UMC_8th.With_Run.user.dto.UserRequestDto.UpdateCourseDTO;
import UMC_8th.With_Run.user.dto.UserResponseDto.MyCourseItemDTO;
import UMC_8th.With_Run.user.dto.UserResponseDto.MyCourseListResultDTO;
import UMC_8th.With_Run.user.entity.User;
import UMC_8th.With_Run.user.repository.RegionCityRepository;
import UMC_8th.With_Run.user.repository.RegionProvinceRepository;
import UMC_8th.With_Run.user.repository.RegionTownRepository;
import UMC_8th.With_Run.user.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MyCourseServiceImpl implements MyCourseService {

    private final ObjectMapper objectMapper;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final PinRepository pinRepository;
    private final RegionProvinceRepository provinceRepository;
    private final RegionCityRepository cityRepository;
    private final RegionTownRepository townRepository;

    @Override
    public MyCourseListResultDTO getMyCourses(HttpServletRequest request) {
        Authentication authentication = jwtTokenProvider.extractAuthentication(request);
        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new GeneralException(ErrorStatus.WRONG_USER));

        List<Course> myCourses = courseRepository.findAllByUserId(user.getId());

        List<MyCourseItemDTO> courseItems = myCourses.stream()
                .map(course -> MyCourseItemDTO.builder()
                        .courseId(course.getId())
                        .courseName(course.getName())
                        .keyword(course.getKeyWord())
                        .time(course.getTime())
                        .courseImage(course.getCourseImage())
                        .location(course.getLocation())
                        .createdAt(course.getCreatedAt())
                        .build())
                .toList();

        return MyCourseListResultDTO.builder()
                .myCourseList(courseItems)
                .build();
    }

    @Transactional
    @Override
    public UserRequestDto.UpdateCourseDTO updateCourse(Long courseId, UpdateCourseDTO dto) {

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.BAD_REQUEST));

        // 1. 코스 정보 수정
        course.setName(dto.getName());
        course.setDescription(dto.getDescription());
        course.setTime(dto.getTime());
        course.setKeyWord(convertToJson(dto.getKeyWords()));

        // 2. 지역 정보 수정
        RegionProvince province = provinceRepository.findById(dto.getProvinceId())
                .orElseThrow(() -> new GeneralException(ErrorStatus.BAD_REQUEST));
        RegionsCity city = cityRepository.findById(dto.getCityId())
                .orElseThrow(() -> new GeneralException(ErrorStatus.BAD_REQUEST));
        RegionsTown town = townRepository.findById(dto.getTownId())
                .orElseThrow(() -> new GeneralException(ErrorStatus.BAD_REQUEST));

        course.setRegionProvince(province);
        course.setRegionsCity(city);
        course.setRegionsTown(town);

        // 3. 핀 정보 업데이트
        List<Pin> pins = pinRepository.findAllById(dto.getPinIds());
        pins.forEach(pin -> pin.setCourse(course)); // course 연관관계 갱신

        // 저장은 @Transactional 로 자동 반영

        return dto;
    }

    private String convertToJson(List<String> list) {
        try {
            return objectMapper.writeValueAsString(list != null ? list : Collections.emptyList());
        } catch (JsonProcessingException e) {
            throw new GeneralException(ErrorStatus.BAD_REQUEST);
        }
    }
}
