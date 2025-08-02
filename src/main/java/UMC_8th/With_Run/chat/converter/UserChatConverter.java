package UMC_8th.With_Run.chat.converter;

import UMC_8th.With_Run.chat.entity.Chat;
import UMC_8th.With_Run.chat.entity.mapping.UserChat;
import UMC_8th.With_Run.user.entity.User;

import java.time.LocalDateTime;

public class UserChatConverter {

    public static UserChat toNewUserChat(User user, Chat chat) {
        return UserChat.builder()
                .user(user)
                .chat(chat)
                .unReadMsg(0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}
