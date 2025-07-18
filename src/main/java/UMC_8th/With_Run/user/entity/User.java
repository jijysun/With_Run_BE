package UMC_8th.With_Run.user.entity;

import UMC_8th.With_Run.chat.entity.Message;
import UMC_8th.With_Run.chat.entity.mapping.UserChat;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.LastModifiedDate;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "naver_id", nullable = false, length = 64)
    private String naverId;

    @Column(nullable = false, length = 50)
    private String email;

    //@CreateDate
    private LocalDate createdAt;

    @LastModifiedDate
    private LocalDate updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User follower;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_user_id")
    private User followee;

    @OneToOne
    @JoinColumn(name = "profile_id")
    private Profile profile;

    @OneToMany (mappedBy = "user", cascade = CascadeType.ALL)
    private List<UserChat> userChatList = new ArrayList<>();

    @OneToMany (mappedBy = "user", cascade = CascadeType.ALL)
    private List<Message> messageList = new ArrayList<>();


}
