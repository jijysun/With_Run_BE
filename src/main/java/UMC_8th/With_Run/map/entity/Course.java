package UMC_8th.With_Run.map.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "course")
@EntityListeners(AuditingEntityListener.class)
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 255, nullable = false)
    private String name;

    @Column(length = 255, nullable = false)
    private String description;

    @Column(length = 255)
    private String keyWord;

    @Column(length = 255)
    private String location;

    // DB 스키마(image_e33565.png)에 따라 'time' 컬럼이 varchar(255)이므로 String 타입으로 매핑
    // 필드 이름과 컬럼 이름이 같으므로 @Column(name="time")은 제거
    @Column(nullable = false, length = 255) // DB 스키마에 NOT NULL, VARCHAR(255)라면
    private String time; // 타입을 String으로 변경

    @Column(length = 255)
    private String courseImage;

    @Column
    private Long userId;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime updatedAt;

    @Column
    private LocalDateTime deletedAt;

    // Pin과의 @OneToMany 연관 관계 매핑을 제거합니다.
    // private List<Pin> pins = new ArrayList<>();
    // public void addPin(Pin pin) { pins.add(pin); pin.setCourse(this); }
}