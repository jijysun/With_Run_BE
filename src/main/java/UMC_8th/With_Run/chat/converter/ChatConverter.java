package UMC_8th.With_Run.chat.converter;

import UMC_8th.With_Run.chat.dto.ChatResponseDTO;
import UMC_8th.With_Run.chat.entity.Chat;
import UMC_8th.With_Run.chat.entity.mapping.UserChat;
import UMC_8th.With_Run.user.entity.Profile;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
public class ChatConverter {

    public static List<ChatResponseDTO.GetChatListDTO> toGetChatListDTO(List<UserChat> userChatList, List<Integer> unReadMsgCount, List<Long> chatIdList, Map<Long, List<UserChat>> otherUserChatList) {
        List<ChatResponseDTO.GetChatListDTO> dto = new ArrayList<>();

        for (int i = 0; i < chatIdList.size(); i++) {
            Long chatId = chatIdList.get(i);
            List<UserChat> participantsList = otherUserChatList.get(chatId);

            UserChat uc = userChatList.get(i);

            List<String> userNameList = participantsList.stream()
                    .map(userChat -> userChat.getUser().getProfile().getName()).toList();

            List<String> userProfileList = participantsList.stream()
                    .map(userChat -> userChat.getUser().getProfile().getProfileImage()).toList();

            dto.add(ChatResponseDTO.GetChatListDTO.builder()
                    .chatId(chatId)
                    .chatName(uc.getChatName())
                    .usernameList(userNameList)
                    .userProfileList(userProfileList)
                    .participants(uc.getChat().getParticipants())
                    .lastReceivedMsg(uc.getChat().getLastReceivedMsg())
                    .unReadMsgCount(unReadMsgCount.get(i))
                    .build());
        }

        return dto;
    }

    public static Chat toNewChatConverter() {
        return Chat.builder()
                .userChatList(new ArrayList<>())
                .messageList(new ArrayList<>())
                .participants(2)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public static List<ChatResponseDTO.GetInviteUserDTO> toGetInviteUserDTO(List<Long> userList, List<Profile> profileList) {
        List<ChatResponseDTO.GetInviteUserDTO> dtoList = new ArrayList<>();

        for (int i = 0; i < userList.size(); i++) {
            dtoList.add(ChatResponseDTO.GetInviteUserDTO.builder()
                    .userId(userList.get(i))
                    .name(profileList.get(i).getName())
                    .profileImage(profileList.get(i).getProfileImage())
                    .build());
        }

        return dtoList;
    }
}
