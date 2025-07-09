package UMC_8th.With_Run.chat.service;

import UMC_8th.With_Run.chat.entity.Chat;
import UMC_8th.With_Run.chat.entity.mapping.UserChat;
import UMC_8th.With_Run.chat.repository.ChatRepository;
import UMC_8th.With_Run.chat.repository.UserChatRepository;
import UMC_8th.With_Run.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatService {

//    private final UserRepository userRepository;
    private final ChatRepository chatRepository;
    private final UserChatRepository userChatRepository;

    public List<Chat> getChatList(Long userId){

        User user = new User();
//        User user = userRepository.findBYId(userId);

//        List<UserChat> allByUserId = userChatRepository.findAllByUserId(userId);
//        if (allByUserId.isEmpty()) throw new ChatHandler(ErrorCode.EMPTY_CHAT_LIST);

        List<UserChat> allByUser = userChatRepository.findAllByUser(user);

        List<Long> chatIds = allByUser.stream()
                .map(userChat -> userChat.getId()).collect(Collectors.toList());

        List<Chat> allChats = chatRepository.findAllById(chatIds);

        return allChats;
    }
}
