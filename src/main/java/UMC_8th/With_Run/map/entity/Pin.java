// src/main/java/UMC_8th/With_Run/map/entity/Pin.java
package UMC_8th.With_Run.map.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter; // 반드시 필요

import java.time.LocalDateTime;

@Entity
@Getter // PinServiceImpl에서 getXXX() 사용하므로 필요
@Setter // PinServiceImpl에서 setXXX() 사용하므로 필요
@NoArgsConstructor
@AllArgsConstructor
@Builder // PinServiceImpl에서 Pin.builder() 사용하므로 필요
@Table(name = "Pin") // 데이터베이스 테이블 이름과 일치
public class Pin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ERD에 따르면 Pin 테이블에는 user_id가 있습니다.
    // PinServiceImpl에서는 현재 userId를 직접적으로 사용하지 않지만, 엔티티에는 필요합니다.
    @Column(name = "user_id")
    private Long userId;

    // PinServiceImpl에서 `courseId`를 사용하므로 추가.
    // 하지만 Pin과 Course의 관계는 `CoursePin` 엔티티가 관리하는 것이 일반적이므로,
    // 이 `course_id`는 Pin이 단일 코스에 종속적일 때만 사용되어야 합니다.
    // 만약 한 Pin이 여러 코스에 사용될 수 있다면 이 필드는 삭제되어야 합니다.
    // 일단 PinServiceImpl의 기존 로직을 위해 포함시킵니다.
    @Column(name = "course_id")
    private Long courseId; // Pin이 특정 코스에 속하는 경우에만 사용

    @Column(name = "name", length = 255)
    private String name;

    @Column(name = "detail", length = 255)
    private String detail;

    // PinServiceImpl에서 `color`를 사용하므로 추가. ERD에 명시되어 있지 않다면 추가해야 합니다.
    @Column(name = "color", length = 50)
    private String color; // 예: 색상 코드 "#FFFFFF" 또는 이름 "red"

    // PinServiceImpl에서 `latitude`와 `longitude`를 사용하므로 추가.
    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    // ERD에 Pin 테이블에는 province_id, city_id, town_id도 있습니다.
    // PinServiceImpl에서 직접 사용하지 않더라도 엔티티에는 필요할 수 있습니다.
    @Column(name = "province_id")
    private Long provinceId;

    @Column(name = "city_id")
    private Long cityId;

    @Column(name = "town_id")
    private Long townId;
}