package UMC_8th.With_Run.chat.dto;

import UMC_8th.With_Run.chat.entity.Message;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ChatResponseDTO {

    @Getter @Builder @NoArgsConstructor @AllArgsConstructor
    public static class CreateChatDTO {
        private Long chatId;
    }

    @Getter @Builder @NoArgsConstructor @AllArgsConstructor
    public static class GetInviteUserDTO {
        private Long userId;

        private String name;

        private String profileImage;
    }

    @Getter @Builder @NoArgsConstructor @AllArgsConstructor
    public static class GetChatListDTO {
        private Long chatId;

        private String chatName;

        private List<String> usernameList; // 참여자 이름은 배열로

        private List<String> userProfileList; // 참여자 프로필, 최대 3개?

        private Integer participants;
    }

    @Getter @Builder @NoArgsConstructor @AllArgsConstructor
    public static class BroadcastInviteUserDTO {
        private Long userId;

        private LocalDateTime createdAt;

        private String msg;

    }

    @Getter @Builder @NoArgsConstructor @AllArgsConstructor
    public static class ChatHistoryDTO {
        private ArrayList<Message> messages;
    }

    @Getter @Builder @NoArgsConstructor @AllArgsConstructor
    public static class BroadcastMsgDTO {
        private Long userId;

        private Long chatId;

        private String userName;

        private String userProfileImage;

        private String msg;

        private boolean isCourse;

        private LocalDateTime createdAt;
    }

    @Getter @Builder @NoArgsConstructor @AllArgsConstructor
    public static class BroadcastCourseDTO {
        private Long userId;

        private Long chatId;

        private String msg;

        private boolean isCourse;

        private Long courseId;

        private String courseImage;

        private String keyword;

        private LocalDateTime createdAt;
    }
}
