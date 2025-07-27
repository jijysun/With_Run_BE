// src/main/java/UMC_8th/With_Run/map/entity/Course.java
package UMC_8th.With_Run.map.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "Course") // 데이터베이스 테이블 이름과 일치
@EntityListeners(AuditingEntityListener.class)
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT", nullable = false)
    private String description;

    // image_eb7fdc.jpg에서 'key_word' 에러. ERD는 VARCHAR(255)지만 image_eab82b.png에서 json으로 표시됨.
    // 현재 코드에서 String으로 처리하므로 VARCHAR(255) 또는 TEXT로 가정.
    @Column(name = "key_word", length = 255)
    private String keyWord;

    // image_eb7fdc.jpg에서 'location' 에러. ERD는 VARCHAR(255)지만 image_eab82b.png에서 bigint로 표시됨.
    // 현재 CourseServiceImpl에서 String으로 쉼표 구분하여 저장하므로, VARCHAR(255) 또는 TEXT로 가정.
    @Column(name = "location", length = 255)
    private String location; // 지역을 쉼표로 구분된 문자열로 저장하는 경우

    // image_eb7fdc.jpg에서 'time' 에러. ERD는 Int지만 image_eab82b.png에서 datetime(6)으로 표시됨.
    // 현재 CourseServiceImpl에서 totalMinutes(int)로 저장하므로, INT 타입으로 가정.
    @Column(name = "time") // ERD의 'time' 기간에 대한 Integer (분 단위)
    private Integer timeInMinutes;

    // image_eb7fdc.jpg에서 'course_image' 에러. ERD는 ImageUrl.
    @Column(name = "course_image", length = 255)
    private String courseImage; // URL 또는 경로로 가정

    @Column(name = "user_id") // 코스를 생성한 사용자 ID
    private Long userId;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt; // ERD에 update_at이 없지만, 일반적인 엔티티 관리용으로 포함.

    // deleted_at은 ERD에 있지만, CoursePin에서는 @EntityListeners(AuditingEntityListener.class)를 사용한다면
    // 일반적으로 소프트 삭제를 위한 필드는 Auditing에 포함되지 않으므로 직접 관리해야 합니다.
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    // Course와 CoursePin의 1:N 관계
    // CascadeType.ALL은 CoursePin의 영속성도 함께 관리합니다.
    // orphanRemoval = true는 부모(Course)에서 제거된 CoursePin 자식 엔티티를 자동으로 DB에서 삭제합니다.
    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CoursePin> coursePins = new ArrayList<>();

    // CoursePin을 추가하는 헬퍼 메서드
    public void addCoursePin(CoursePin coursePin) {
        coursePins.add(coursePin);
        coursePin.setCourse(this); // CoursePin 쪽에도 Course 참조 설정
    }
}