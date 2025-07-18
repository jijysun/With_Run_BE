package UMC_8th.With_Run.chat.dto;


import io.swagger.v3.oas.annotations.Parameter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class ChatRequestDTO {

    @Builder @Getter @NoArgsConstructor @AllArgsConstructor
    public static class CreateChatReqDTO {
        private Integer userId;
        private Integer targetUserId;
    }

    @Builder @Getter @NoArgsConstructor @AllArgsConstructor
    public static class RenameChatDTO {
        private Integer userId;
        private Integer chatId;
        private String newName;
    }
}
