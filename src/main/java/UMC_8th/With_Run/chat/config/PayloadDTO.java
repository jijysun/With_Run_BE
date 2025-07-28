package UMC_8th.With_Run.chat.config;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PayloadDTO <T> {

    private String type;

    private T payload;
}
