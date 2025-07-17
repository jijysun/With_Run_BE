package UMC_8th.With_Run.chat.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Builder @Getter
@NoArgsConstructor @AllArgsConstructor
public class Message {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "chat_id")
    private Chat chat;

    private Boolean isCourse;
    private String msg;
    private LocalDate createdAt;
    private LocalDate updatedAt;

}