package UMC_8th.With_Run.chat.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDate;

@Entity @Getter @Builder
@NoArgsConstructor
@AllArgsConstructor
public class Chat {

    @Id
    @GeneratedValue
    private Long id;

    @Size(min = 3, max = 20)
    private String name;

    @CreatedDate
    private LocalDate createdAt;

    @UpdateTimestamp
    private LocalDate updatedAt;

}
