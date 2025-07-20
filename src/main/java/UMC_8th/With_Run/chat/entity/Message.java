package UMC_8th.With_Run.chat.entity;

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
import java.time.LocalDateTime;

@Entity
@Builder @Getter
@NoArgsConstructor @AllArgsConstructor
public class Message {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "chat_id")
    private Chat chat;

    private Boolean isCourse; // Enum Type 으로 변경해도 될 듯?

    @Column(columnDefinition = "TEXT")
    private String msg;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

}