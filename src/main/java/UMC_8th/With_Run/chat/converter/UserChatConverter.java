package UMC_8th.With_Run.chat.converter;

import UMC_8th.With_Run.chat.entity.Chat;
import UMC_8th.With_Run.chat.entity.mapping.UserChat;
import UMC_8th.With_Run.user.entity.User;

import java.time.LocalDateTime;

public class UserChatConverter {

    public static UserChat toNewUserChat(User user, User targetUser, Chat chat) {
        return UserChat.builder()
                .user(user)
                .chat(chat)
                .chatName(targetUser.getProfile().getName())
                .unReadMsg(0)
                .isChatting(false)
                .isDefaultChatName(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    ///  두 Converter 합칠 것!

    public static UserChat toNewUserChatInInvite(User user, String chatName, Chat chat) {
        return UserChat.builder()
                .user(user)
                .chat(chat)
                .chatName(chatName)
                .unReadMsg(0)
                .isChatting(false)
                .isDefaultChatName(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}
