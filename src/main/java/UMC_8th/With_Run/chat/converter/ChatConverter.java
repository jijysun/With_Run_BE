package UMC_8th.With_Run.chat.converter;

import UMC_8th.With_Run.chat.dto.ChatResponseDTO;
import UMC_8th.With_Run.chat.entity.Chat;
import UMC_8th.With_Run.user.entity.Profile;
import UMC_8th.With_Run.user.entity.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ChatConverter {

    public static List<ChatResponseDTO.getChatListDTO> toGetChatListDTO (List<Chat> chatList){
        List<ChatResponseDTO.getChatListDTO> chatListDTOs = new ArrayList<>();
        chatList.stream().map(chat ->
                chatListDTOs.add(ChatResponseDTO.getChatListDTO.builder()
                        .chatId(chat.getId())
                        .chatName(chat.getName())
                        .participants(chat.getParticipants())
                        .build()));

        return chatListDTOs;
    }

    public static Chat toNewChatConverter (Profile profile, Profile targetProfile) {
        return Chat.builder()
                .name(profile.getName() + ", " + targetProfile.getName()) // 이름 접근 시 Profile 에도 접근해야 해요
                .userChatList(new ArrayList<>())
                .messageList(new ArrayList<>())
                .participants(2)
                .createdAt(LocalDate.now())
                .updatedAt(LocalDate.now())
                .build();
    }
}
