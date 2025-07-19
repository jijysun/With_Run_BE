package UMC_8th.With_Run.chat.entity;


import UMC_8th.With_Run.chat.entity.mapping.UserChat;
import UMC_8th.With_Run.user.entity.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Chat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 15)
    private String name;

    @Column(nullable = false)
    private Integer participants; // 참여자 수 입니다.

    @CreatedDate
    private LocalDate createdAt;

    @LastModifiedDate
    private LocalDate updatedAt;

    @OneToMany(mappedBy = "chat", cascade = CascadeType.ALL)
    private List<UserChat> userChatList = new ArrayList<>();

    @OneToMany(mappedBy = "chat", cascade = CascadeType.ALL)
    private List<Message> messageList = new ArrayList<>();

    public void renameChat (String newName){
        this.name = newName;
    }

    public void updateParticipants (int participants){
        this.participants = participants;
    }

    public void addUserChat (UserChat userChat){
        this.getUserChatList().add(userChat);
    }
}


