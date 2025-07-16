package UMC_8th.With_Run.chat.service;

import UMC_8th.With_Run.chat.entity.Chat;
import UMC_8th.With_Run.chat.entity.mapping.UserChat;
import UMC_8th.With_Run.chat.repository.ChatRepository;
import UMC_8th.With_Run.chat.repository.UserChatRepository;
import UMC_8th.With_Run.common.apiResponse.status.ErrorCode;
import UMC_8th.With_Run.common.exception.handler.ChatHandler;
import UMC_8th.With_Run.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatService {

    //    private final UserRepository userRepository;
    private final ChatRepository chatRepository;
    private final UserChatRepository userChatRepository;

    public List<Chat> getChatList(Long userId) {

        User user = User.builder()
                .email("testEmail")
                .naverId("testNaverId")
                .createdAt(LocalDate.now())
                .build();
//        User user = userRepository.findById(userId);
        List<UserChat> userChats = userChatRepository.findAllByUserId(userId);
        if (userChats.isEmpty()) throw new ChatHandler(ErrorCode.EMPTY_CHAT_LIST);

        List<UserChat> allByUser = userChatRepository.findAllByUser(user);

        List<Long> chatIds = allByUser.stream()
                .map(userChat -> userChat.getId()).collect(Collectors.toList());

        List<Chat> allChats = chatRepository.findAllById(chatIds);

        return allChats;
    }

    public void leaveChat(Long userId) {
//        User user = userRepository.findById(userId);
//        List<UserChat> allByUserId = userChatRepository.findAllByUserId(userId);
//        if (allByUserId.isEmpty()) throw new ChatHandler(ErrorCode.EMPTY_CHAT_LIST);
        if (userChatRepository.existsById(userId))
            throw new ChatHandler(ErrorCode.EMPTY_CHAT_LIST);

        userChatRepository.deleteById(userId);

    }


    @Transactional
    public void inviteUser(Long roomId, List<Long> userIdList) {
        Chat chat = chatRepository.findById(roomId).orElseThrow(() -> new ChatHandler(ErrorCode.EMPTY_CHAT_LIST));

        // 받은 id에 대한 여러 user 조회
//        List<User> users = userRepository.findAllByIdIn(userIdList);
        List<User> users = new ArrayList<>();

                // userChat 여러 개 저장
        List<UserChat> newUserList = new ArrayList<>();
        for (User user : users) {
            newUserList.add(UserChat.builder()
                    .chat(chat)
                    .user(user)
                    .build());
        }
        // chat : participants 증가 시키기
        Integer participants = chat.getParticipants();
        chat.updateParticipants(participants + users.size());

        userChatRepository.saveAll(newUserList);

    }

    @Transactional
    public void renameChat(Long roomId, String newName) {
        Chat chat = chatRepository.findById(roomId).orElseThrow(() -> new ChatHandler(ErrorCode.EMPTY_CHAT_LIST));
        chat.renameChat(newName);
    }
}
