package UMC_8th.With_Run.chat.service;

import UMC_8th.With_Run.chat.dto.ChatRequestDTO;
import UMC_8th.With_Run.chat.dto.ChatResponseDTO;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface ChatService {
    List<ChatResponseDTO.GetChatListDTO> getChatList(HttpServletRequest request);

    void createChat(Long targetId, HttpServletRequest request);

    ChatResponseDTO.RenameChatDTO renameChat(Long chatId, String newName, HttpServletRequest request);

    List<ChatResponseDTO.GetInviteUserDTO> getInviteUser(Long chatId, HttpServletRequest request);

    void inviteUser(Long chatId, ChatRequestDTO.InviteUserReqDTO reqDTO, HttpServletRequest request);

    List<ChatResponseDTO.BroadcastMsgDTO> enterChat(Long chatId, HttpServletRequest request);

    void leaveChat(Long chatId, HttpServletRequest request);

    void deleteChat(Long chatId, HttpServletRequest request);
}
