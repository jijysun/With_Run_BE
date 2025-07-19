package UMC_8th.With_Run.chat.converter;

import UMC_8th.With_Run.chat.dto.ChatResponseDTO;
import UMC_8th.With_Run.chat.entity.Chat;
import UMC_8th.With_Run.user.entity.Profile;
import UMC_8th.With_Run.user.entity.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ChatConverter {

    public static List<ChatResponseDTO.getChatListDTO> toGetChatListDTO(List<Chat> chatList) {
        return chatList.stream()
                .map(chat -> ChatResponseDTO.getChatListDTO.builder()
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
                .createdAt(LocalDate.now())
                .updatedAt(LocalDate.now())
                .build();
    }

    public static List<ChatResponseDTO.getInviteUser> toGetInviteUserDTO(List<User> userList, List<Profile> profileList) {
        List<ChatResponseDTO.getInviteUser> dtoList = new ArrayList<>();

        for (int i = 0; i < userList.size(); i++) {
            dtoList.add(ChatResponseDTO.getInviteUser.builder()
                    .userId(userList.get(0).getId())
                    .name(profileList.get(0).getName())
                    .profileImage(profileList.get(0).getProfileImage())
                    .build());
        }

        return dtoList;
    }
}
