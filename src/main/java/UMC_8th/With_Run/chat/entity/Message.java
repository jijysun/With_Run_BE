package UMC_8th.With_Run.chat.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Builder @Getter
@NoArgsConstructor @AllArgsConstructor
public class Message {

    private Integer id;
    private Boolean isCourse;
    private String msg;
    private LocalDate createdAt;
    private LocalDate updatedAt;

}