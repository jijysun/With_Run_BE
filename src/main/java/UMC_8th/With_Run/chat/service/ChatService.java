package UMC_8th.With_Run.chat.service;

import UMC_8th.With_Run.chat.dto.ChatRequestDTO;
import UMC_8th.With_Run.chat.dto.ChatResponseDTO;
import UMC_8th.With_Run.user.entity.User;

import java.util.List;

public interface ChatService {
    List<ChatResponseDTO.GetChatListDTO> getChatList(User user);

    List<ChatResponseDTO.BroadcastMsgDTO> createChat(Long targetId, User user);

    ChatResponseDTO.RenameChatDTO renameChat(Long chatId, String newName, User user);

    List<ChatResponseDTO.GetInviteUserDTO> getInviteUser(Long chatId, User user);

    void inviteUser(Long chatId, ChatRequestDTO.InviteUserReqDTO reqDTO, User user);

    List<ChatResponseDTO.BroadcastMsgDTO> enterChat(Long chatId, User user);

    void leaveChat(Long chatId, User user);

    void deleteChat(Long chatId, User user);
}
