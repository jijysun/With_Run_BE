package UMC_8th.With_Run.user.service;

import UMC_8th.With_Run.common.apiResponse.status.ErrorStatus;
import UMC_8th.With_Run.common.exception.GeneralException;
import UMC_8th.With_Run.common.security.jwt.JwtTokenProvider;
import UMC_8th.With_Run.course.entity.Course;
import UMC_8th.With_Run.course.repository.CourseRepository;
import UMC_8th.With_Run.user.dto.UserResponseDto.MyCourseItemDTO;
import UMC_8th.With_Run.user.dto.UserResponseDto.MyCourseListResultDTO;
import UMC_8th.With_Run.user.entity.User;
import UMC_8th.With_Run.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MyCourseServiceImpl implements MyCourseService {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;

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
}
