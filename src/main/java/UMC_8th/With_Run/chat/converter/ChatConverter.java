package UMC_8th.With_Run.chat.converter;

import UMC_8th.With_Run.chat.dto.ChatResponseDTO;
import UMC_8th.With_Run.chat.entity.Chat;

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
}
