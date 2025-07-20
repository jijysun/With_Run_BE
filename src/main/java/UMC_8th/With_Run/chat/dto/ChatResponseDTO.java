package UMC_8th.With_Run.chat.dto;

import UMC_8th.With_Run.chat.entity.Chat;
import UMC_8th.With_Run.chat.entity.Message;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class ChatResponseDTO {

    @Getter @Builder @NoArgsConstructor @AllArgsConstructor
    public static class createChatDTO {
        private Long chatId;
    }

    @Getter @Builder @NoArgsConstructor @AllArgsConstructor
    public static class getInviteUser{
        private Long userId;

        private String name;

        private String profileImage;
    }


    @Getter @Builder @NoArgsConstructor @AllArgsConstructor
    public static class getChatListDTO {
        private Long chatId;

        private String chatName;

        private ArrayList<String> users; // 참여자 이름은 배열로

        private ArrayList<?> userProfiles; // 참여자 프로필, 최대 3개?

        private Integer participants;

        // 마지막으로 받은 메세지, 그 시간을 어떻게...?
        private LocalDate lastMsgReceived;
    }

    @Getter @Builder @NoArgsConstructor @AllArgsConstructor
    public static class chatHistoryDTO {
        private ArrayList<Message> messages;
    }

    @Getter @Builder @NoArgsConstructor @AllArgsConstructor
    public static class BroadcastMsgDTO {
        private Long userId;

        private String userName;

        private String userProfileImage;

        private String msg;

        private boolean isCourse;

        private LocalDateTime createdAt;
    }
}
