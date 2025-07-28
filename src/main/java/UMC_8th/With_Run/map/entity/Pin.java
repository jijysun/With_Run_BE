package UMC_8th.With_Run.map.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "pin")
public class Pin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // DB 스키마(image_e2c4e3.png)에 따라 'course_id'가 bigint 일반 컬럼이므로 Long으로 매핑.
    // 필드 이름 'courseId'가 DB 컬럼 이름 'course_id'와 자동 매핑되므로 @Column(name="course_id")는 제거
    @Column(nullable = false) // 'course_id' 컬럼이 NOT NULL인 경우
    private Long courseId;

    @Column(nullable = false)
    private Integer pinOrder;

    // DB 스키마(image_e2c4e3.png)에 존재하는 user_id, province_id, city_id, town_id
    @Column
    private Long userId;

    @Column(length = 255)
    private String name;

    @Column(length = 255)
    private String detail;

    @Column(length = 50)
    private String color;

    @Column
    private Double latitude;

    @Column
    private Double longitude;

    @Column
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime updatedAt;

    @Column
    private LocalDateTime deletedAt;

    @Column
    private Long provinceId;

    @Column
    private Long townId;

    @Column
    private Long cityId;
}