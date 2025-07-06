package UMC_8th.With_Run.chat.dto;

import UMC_8th.With_Run.chat.entity.Chat;
import UMC_8th.With_Run.chat.entity.Message;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;

public class ChatResponseDTO {

    @Getter @Builder @NoArgsConstructor @AllArgsConstructor
    public static class createChatDTO {
        private Integer chatId;
    }

    @Getter @Builder @NoArgsConstructor @AllArgsConstructor
    public static class getChatListDTO {
        private Long chatId;

        private ArrayList<String> users; // 참여자 이름은 배열로

        private ArrayList<?> userProfiles; // 참여자 프로필, 최대 3개?

        private Integer participants;

        private LocalDate lastMsgReceived;
    }

    @Getter @Builder @NoArgsConstructor @AllArgsConstructor
    public static class chatHistoryDTO {
        private ArrayList<Message> messages;
    }

    @Getter @Builder @NoArgsConstructor @AllArgsConstructor
    public static class TestDTO{
        private String test;
        private Integer testCode;
    }
}
