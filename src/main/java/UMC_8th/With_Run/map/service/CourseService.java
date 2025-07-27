package UMC_8th.With_Run.map.service;


import UMC_8th.With_Run.map.dto.MapRequestDTO; // DTO 패키지는 유지

public interface CourseService {
    /**
     * 새로운 산책 코스를 생성합니다.
     * @param userId 코스를 생성하는 사용자의 ID (이미 인증되어 전달됨)
     * @param requestDto 코스 생성 요청 데이터
     * @return 생성된 코스의 ID
     */
    Long createCourse(Long userId, MapRequestDTO.CourseCreateRequestDto requestDto);

    // 향후 코스 조회, 수정, 삭제 등의 메서드가 이 인터페이스에 추가될 수 있습니다.
}