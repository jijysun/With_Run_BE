package UMC_8th.With_Run.chat.entity.mapping;

import UMC_8th.With_Run.chat.entity.Chat;
import UMC_8th.With_Run.user.entity.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserChat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, columnDefinition = "BIGINT")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) // EAGER ?!?!
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_id")
    @JsonIgnore
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Chat chat;

    @Column(nullable = false)
    private Boolean isDefaultChatName = true;

    @Column(length = 15)
    private String chatName;

    @Column(nullable = false)
    private Integer unReadMsg;

    @Column(nullable = false)
    private Boolean isChatting;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    public void renameChat (String newName){
        this.isDefaultChatName = false;
        this.chatName = newName;
    }

    public void setToChatting() {
        this.unReadMsg = 0;
        this.isChatting = true;
    }

    public void setToNotChatting() {
        this.isChatting = false;
    }

    public void updateUnReadMsg() {
        this.unReadMsg ++;
    }
}
