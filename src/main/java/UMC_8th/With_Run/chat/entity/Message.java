package UMC_8th.With_Run.chat.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity @Builder @Getter
@NoArgsConstructor @AllArgsConstructor
public class Message {
    @Id @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Integer id;

}