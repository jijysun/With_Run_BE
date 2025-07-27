// src/main/java/UMC_8th/With_Run/map/entity/Course.java
package UMC_8th.With_Run.map.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter // 필요에 따라 추가
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "course") // DB 테이블 이름과 정확히 일치 (image_eab82b.png 참고, 소문자 'course'로 가정)
@EntityListeners(AuditingEntityListener.class)
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", length = 255, nullable = false) // DB 스키마에 VARCHAR(255)
    private String name;

    @Column(name = "description", length = 255, nullable = false) // DB 스키마에 VARCHAR(255)
    private String description;

    // image_eab82b.png에 따르면 DB의 key_word는 'json' 타입.
    // DB 스키마를 VARCHAR(255)로 변경하여 String으로 매핑하는 것을 가정합니다.
    @Column(name = "key_word", length = 255)
    private String keyWord;

    // image_eab82b.png에 따르면 DB의 location은 'bigint' 타입.
    // DB 스키마를 VARCHAR(255)로 변경하여 String으로 매핑하는 것을 가정합니다.
    @Column(name = "location", length = 255)
    private String location;

    // image_eab82b.png에 따르면 DB의 time은 'datetime(6)' 타입.
    // DB 스키마를 INT로 변경하여 Integer로 매핑하는 것을 가정합니다.
    @Column(name = "time")
    private Integer timeInMinutes;

    @Column(name = "course_image", length = 255) // DB 스키마에 VARCHAR(255)
    private String courseImage;

    @Column(name = "user_id") // ERD (image_de5b46.jpg) 에 존재
    private Long userId;

    @CreatedDate
    @Column(name = "created_at", updatable = false) // DB 스키마에 datetime(6)
    private LocalDateTime createdAt;

    @Column(name = "updated_at") // DB 스키마에 datetime(6)으로 추정
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at") // DB 스키마에 datetime(6)으로 추정
    private LocalDateTime deletedAt;

    // Course와 Pin의 1:N 관계
    // mappedBy는 Pin 엔티티의 'course' 필드를 참조합니다.
    // CascadeType.ALL은 Course가 저장/삭제될 때 Pin도 함께 관리합니다.
    // orphanRemoval = true는 Course에서 제거된 Pin이 자동으로 DB에서 삭제되도록 합니다.
    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    // 순서를 유지하기 위한 @OrderBy 어노테이션 추가 (Pin 엔티티의 pinOrder 필드 기준)
    @OrderBy("pinOrder ASC")
    private List<Pin> pins = new ArrayList<>();

    // Pin을 추가하는 헬퍼 메서드
    public void addPin(Pin pin) {
        pins.add(pin);
        pin.setCourse(this); // Pin 쪽에도 Course 참조 설정
    }
}