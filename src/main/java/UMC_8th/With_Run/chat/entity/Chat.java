package UMC_8th.With_Run.chat.entity;


import java.time.LocalDate;

// @Entity @Getter @Builder @NoArgsConstructor @AllArgsConstructor
public class Chat {

    // @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // @Column(nullable = false, length = 20)
    private String name;

    // @Column(nullable = false)
    private Integer participants; // 참여자 수 입니다.

    // @CreateDate
    private LocalDate createdAt;

    // @LastModifiedDate
    private LocalDate updatedAt;

}
