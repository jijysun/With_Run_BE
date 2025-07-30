package UMC_8th.With_Run.map.service;


import UMC_8th.With_Run.map.dto.MapRequestDTO; // DTO 패키지는 유지

public interface CourseService {

    Long createCourse(Long userId, MapRequestDTO.CourseCreateRequestDto requestDto);

}