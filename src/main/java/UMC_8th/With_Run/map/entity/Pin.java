package UMC_8th.With_Run.map.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Pin {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long courseId;
    private String name;
    private String color;
    private String detail;
    private Double latitude;
    private Double longitude;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
}
