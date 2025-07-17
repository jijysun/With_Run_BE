package UMC_8th.With_Run.chat.service;

import UMC_8th.With_Run.chat.converter.ChatConverter;
import UMC_8th.With_Run.chat.converter.UserChatConverter;
import UMC_8th.With_Run.chat.entity.Chat;
import UMC_8th.With_Run.chat.entity.Message;
import UMC_8th.With_Run.chat.entity.mapping.UserChat;
import UMC_8th.With_Run.chat.repository.ChatRepository;
import UMC_8th.With_Run.chat.repository.MessageRepository;
import UMC_8th.With_Run.chat.repository.UserChatRepository;
import UMC_8th.With_Run.common.apiResponse.status.ErrorCode;
import UMC_8th.With_Run.common.exception.handler.ChatHandler;
import UMC_8th.With_Run.user.entity.Profile;
import UMC_8th.With_Run.user.entity.User;
import UMC_8th.With_Run.user.repository.ProfileRepository;
import UMC_8th.With_Run.user.repository.UserRepository;
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

    private final UserRepository userRepository;
    private final ChatRepository chatRepository;
    private final UserChatRepository userChatRepository;
    private final ProfileRepository profileRepository;
    private final MessageRepository messageRepository;

    @Transactional
    public void createChat(Long targetId) {
//        User user = userRepository.findById(userId); // jwt
        User user =  User.builder().build();
        Profile userProfile = profileRepository.findByUser(user);

//        User targetUser = userRepository.findById (targetId); // targetUser는 비영속 상태이다, targetUser에 대한 update, save는 필요
        User targetUser = User.builder().build();
        Profile targetProfile = profileRepository.findByUser(targetUser);

        Chat chat = ChatConverter.toNewChatConverter(userProfile, targetProfile);

        List<UserChat> userChats = new  ArrayList<>();
        userChats.add(UserChatConverter.toUserChat(user, chat));
        userChats.add(UserChatConverter.toUserChat(targetUser, chat));

        chat.addUserChat(userChats.get(0));
        chat.addUserChat(userChats.get(1));

        chatRepository.save(chat);
        userChatRepository.saveAll(userChats);
    }

    @Transactional
    public void inviteUser(Long roomId, List<Long> userIdList) {
//        List<User> users = userRepository.findAllByIdIn(userIdList); // 받은 id에 대한 여러 user 조회, JWT
        List<User> users = new ArrayList<>();

        Chat chat = chatRepository.findById(roomId).orElseThrow(() -> new ChatHandler(ErrorCode.EMPTY_CHAT_LIST));

        // userChat 여러 개 저장
        List<UserChat> newUserList = new ArrayList<>();
        for (User user : users) {
            newUserList.add(UserChat.builder()
                    .chat(chat)
                    .user(user)
                    .build());
        }

        // chat : participants 증가 시키기
        chat.updateParticipants(chat.getParticipants() + users.size());

        userChatRepository.saveAll(newUserList);
    }

    @Transactional
    public void renameChat(Long roomId, String newName) {
        Chat chat = chatRepository.findById(roomId).orElseThrow(() -> new ChatHandler(ErrorCode.EMPTY_CHAT_LIST));
        chat.renameChat(newName);
    }

    public List<Message> enterChat (Long roomId) {
        Chat chat = chatRepository.findById(roomId).orElseThrow(() -> new ChatHandler(ErrorCode.EMPTY_CHAT_LIST));
        return messageRepository.findByChat(chat);
    }

    @Transactional
    public void leaveChat(Long chatId) {
//        User user = userRepository.findById(userId); // jwt
        User user = User.builder().build();
        Chat chat = chatRepository.findById(chatId).orElseThrow(() -> new ChatHandler(ErrorCode.EMPTY_CHAT_LIST));

        userChatRepository.deleteUserChatByUserAndChat(user, chat);

        chat.updateParticipants(chat.getParticipants()-1);
    }


    public List<Chat> getChatList(Long userId) {

        User user = User.builder()
                .email("testEmail")
                .naverId("testNaverId")
                .createdAt(LocalDate.now())
                .build();
//        User user = userRepository.findById(userId); // jwt
        List<UserChat> userChats = userChatRepository.findAllByUserId(userId);
        if (userChats.isEmpty()) throw new ChatHandler(ErrorCode.EMPTY_CHAT_LIST);

        List<UserChat> allByUser = userChatRepository.findAllByUser(user);

        List<Long> chatIds = allByUser.stream()
                .map(userChat -> userChat.getId()).collect(Collectors.toList());

        List<Chat> allChats = chatRepository.findAllById(chatIds);

        return allChats;
    }

}