package UMC_8th.With_Run.chat.converter;

import UMC_8th.With_Run.chat.dto.ChatResponseDTO;
import UMC_8th.With_Run.chat.entity.Chat;
import UMC_8th.With_Run.user.entity.Profile;
import UMC_8th.With_Run.user.entity.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ChatConverter {

    public static List<ChatResponseDTO.GetChatListDTO> toGetChatListDTO(List<Chat> chatList) {
        return chatList.stream()
                .map(chat -> ChatResponseDTO.GetChatListDTO.builder()
                        .chatId(chat.getId())
                        .chatName(chat.getName())
                        .participants(chat.getParticipants())
                        .build())
                .collect(Collectors.toList());
    }

    public static Chat toNewChatConverter (Profile profile, Profile targetProfile) {
        return Chat.builder()
                .name(profile.getName() + ", " + targetProfile.getName()) // 이름 접근 시 Profile 에도 접근해야 해요
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
