// src/main/java/UMC_8th/With_Run/map/entity/CoursePin.java
package UMC_8th.With_Run.map.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter; // setCourse(), setPin() 사용 위해 필요

@Entity
@Getter
@Setter // Course.addCoursePin()에서 setCourse()를 호출하므로 필요
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "CoursePin", uniqueConstraints = @UniqueConstraint(columnNames = {"course_id", "pin_id"})) // ERD에 따른 테이블 이름
public class CoursePin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false) // 'course_id' 컬럼에 매핑
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pin_id", nullable = false) // 'pin_id' 컬럼에 매핑
    private Pin pin;

    @Column(name = "pin_order", nullable = false)
    private Integer pinOrder; // 코스 내에서 핀의 순서를 유지하기 위함
}