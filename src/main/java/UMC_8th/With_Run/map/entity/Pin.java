// src/main/java/UMC_8th/With_Run/map/entity/Pin.java
package UMC_8th.With_Run.map.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter; // Lombok Setter 어노테이션 추가

import java.time.LocalDateTime;

@Entity
@Getter // 모든 필드에 대한 getter 자동 생성
@Setter // 모든 필드에 대한 setter 자동 생성
@NoArgsConstructor
@AllArgsConstructor
@Builder // 빌더 패턴 자동 생성
@Table(name = "pin") // DB 테이블 이름과 정확히 일치 (image_eb9662.png 참고, 소문자 'pin'으로 가정)
public class Pin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Pin이 속한 Course에 대한 ManyToOne 관계
    // 'course_id' 컬럼이 Pin 테이블의 외래 키가 됩니다.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false) // Pin은 반드시 하나의 Course에 속하므로 nullable = false
    private Course course; // Course 엔티티를 직접 참조

    // Pin이 코스 내에서 가지는 순서 유지 (image_de5820.jpg 에서 순서 지정 UI 확인)
    @Column(name = "pin_order", nullable = false) // Pin 테이블에 직접 추가
    private Integer pinOrder;

    @Column(name = "user_id") // DB 스키마에 존재
    private Long userId;

    @Column(name = "name", length = 255) // DB 스키마에 존재
    private String name;

    @Column(name = "detail", length = 255) // DB 스키마에 존재
    private String detail;

    @Column(name = "color", length = 50) // DB 스키마에 존재
    private String color;

    @Column(name = "latitude") // DB 스키마에 존재
    private Double latitude;

    @Column(name = "longitude") // DB 스키마에 존재
    private Double longitude;

    @Column(name = "created_at") // DB 스키마에 존재
    private LocalDateTime createdAt;

    @Column(name = "updated_at") // DB 스키마에 존재
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at") // DB 스키마에 존재
    private LocalDateTime deletedAt;

    @Column(name = "province_id") // ERD (image_de5b46.jpg) 에 존재
    private Long provinceId;


    private Long townId;
}