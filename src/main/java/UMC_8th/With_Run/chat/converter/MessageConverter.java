package UMC_8th.With_Run.chat.converter;

import UMC_8th.With_Run.chat.dto.ChatResponseDTO;
import UMC_8th.With_Run.chat.entity.Message;
import UMC_8th.With_Run.user.entity.Profile;
import UMC_8th.With_Run.user.entity.User;

public class MessageConverter {

    public static ChatResponseDTO.BroadcastMsgDTO toBroadCastMsgDTO (Long userId,Profile profile, Message message) {
        return ChatResponseDTO.BroadcastMsgDTO.builder()
                .userId(userId)
                .userName(profile.getName())
                .userProfileImage(profile.getProfileImage())
                .msg(message.getMsg())
                .isCourse(false)
                .createdAt(message.getCreatedAt())
                .build();
    }
}
