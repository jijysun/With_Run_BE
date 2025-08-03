package UMC_8th.With_Run.course.entity;

import UMC_8th.With_Run.map.entity.RegionProvince;
import UMC_8th.With_Run.map.entity.RegionsCity;
import UMC_8th.With_Run.map.entity.RegionsTown;
import UMC_8th.With_Run.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "Course")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    @Column(columnDefinition = "json")
    private String keyWord;

    private Integer time;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private LocalDateTime deletedAt;

    private String courseImage;

    //private Long userId; //추가

    private String location;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private RegionProvince regionProvince;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private RegionsCity regionsCity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private RegionsTown regionsTown;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}
